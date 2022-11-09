package com.wko.dothings.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.druid.util.StringUtils;
import com.wko.dothings.common.base.Page;
import com.wko.dothings.common.base.Response;
import com.wko.dothings.task.SyncTask;
import com.wko.dothings.utils.DateUtils;
import com.wko.dothings.utils.IdGenerator;
import com.wko.dothings.wallet.dao.WalletMapper;
import com.wko.dothings.wallet.entities.*;
import com.wko.dothings.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    @Resource
    private WalletMapper walletMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private SyncTask syncTask;

    public static final String BALANCE_PURCHASE_KEY_PREFIX = "lock:balance:purchase:";

    public static final String LOCK_BALANCE_REFUND_KEY_PREFIX = "lock:balance:refund:";

    public static final String ORDER_BALANCE_REFUND_KEY_PREFIX = "order:balance:refund:";

    public static final String ENTRY_ADD = "1";
    public static final String ENTRY_SUB = "0";

    public static final String CANCEL_ORDER = "1";
    public static final String REFUND_COMPLETE = "2";
    public static final String REFUND_WAIT = "0";


    /**
     * 根据用户id查询钱包余额
     *
     * @param userId 用户id
     * @return 钱包余额
     */
    @Override
    public int queryUserWalletBalance(String userId) {
        return walletMapper.queryUserWalletBalance(userId);
    }

    /**
     * 用户消费指定金额（扣余额）
     *
     * @param purchaseOrder 购买订单信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response userSmartPay(PurchaseOrder purchaseOrder) {

        String userId = purchaseOrder.getUserId();
        Long orderId = purchaseOrder.getOrderId();

        //查询钱包余额
        int userBalance = walletMapper.queryUserWalletBalance(userId);
        if (userBalance < 0) {
            return Response.error("余额异常！");
        }

        RLock lock = redissonClient.getLock(BALANCE_PURCHASE_KEY_PREFIX + userId + orderId);
        try {
            //尝试获取锁 最大等待时间1s 锁自动释放时间10s
            boolean tryLock = lock.tryLock(5, 30, TimeUnit.SECONDS);
            if (!tryLock) {
                return Response.error("用户订单无法获取锁");
            }

            //更新余额
            int remain = userBalance - purchaseOrder.getPurchaseAmount();
            if (remain < 0) {
                return Response.error("用户余额不足，无法购买！");
            }
            int subFlag = walletMapper.updateUserBalance(userId, remain);
            if (subFlag > 0) {
                log.info("用户{}的订单{}余额扣减完毕！", userId, orderId);
            }

            //更新订单状态
            String nowTime = DateUtils.nowDateStr();
            int success = walletMapper.completePurchaseOrder(orderId, nowTime);
            if (success > 0) {
                log.info("用户{}的订单{}已完成！", userId, orderId);
            }

            //插入记录至钱包余额明细表
            WalletBalanceRecord record = transferBean(purchaseOrder, nowTime);
            int updateFlag = walletMapper.insertBalanceRecord(record);
            if (updateFlag > 0) {
                log.info("用户{}的订单{}明细记录完成", userId, orderId);
            }
        } catch (InterruptedException e) {
            log.error("获取锁异常:{}", e.getMessage());
            return Response.error("获取锁异常:" + e.getMessage());
        } finally {
            //释放锁
            lock.unlock();
        }
        return Response.ok("购买成功");
    }

    /**
     * 构建余额明细信息对象
     */
    @Transactional(rollbackFor = Exception.class)
    public WalletBalanceRecord transferBean(PurchaseOrder purchaseOrder, String createTime) {
        WalletBalanceRecord record = new WalletBalanceRecord();
        record.setUserId(purchaseOrder.getUserId());
        //改为查询Redis缓存
        record.setWalletId(walletMapper.queryUserWalletId(purchaseOrder.getUserId()));
        record.setIsEntry(ENTRY_SUB);
        record.setCreateTime(createTime);
        record.setBalanceChange(purchaseOrder.getPurchaseAmount());
        record.setOrderId(purchaseOrder.getOrderId());
        return record;
    }


    /**
     * 用户退款
     *
     * @param refundOrderVO 退款订单信息
     * @return 成功/失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response userRefund(RefundOrderVO refundOrderVO) {

        //生成退款订单id
        Long refundOrderId = idGenerator.nextId(ORDER_BALANCE_REFUND_KEY_PREFIX);
        String userId = refundOrderVO.getUserId();

        //查询余额
        int userBalance = walletMapper.queryUserWalletBalance(userId);
        if (userBalance < 0) {
            return Response.error("余额异常！");
        }

        RLock lock = redissonClient.getLock(LOCK_BALANCE_REFUND_KEY_PREFIX + userId + refundOrderId);
        try {
            //尝试获取锁 最大等待时间1s 锁自动释放时间10s
            boolean tryLock = lock.tryLock(10, 100, TimeUnit.SECONDS);

            if (!tryLock) {
                return Response.error("用户订单无法获取锁");
            }

            //写入退款订单
            String nowTime = DateUtils.nowDateStr();
            RefundOrder refundOrder = new RefundOrder();
            BeanUtil.copyProperties(refundOrderVO, refundOrder);
            //标志位：退款未完成
            refundOrder.setIsSuccess(REFUND_WAIT);
            refundOrder.setCreateTime(nowTime);
            refundOrder.setRefundOrderId(refundOrderId);
            int writeFlag = walletMapper.insertRefundOrder(refundOrder);
            if (writeFlag > 0) {
                log.info("用户退款订单：{}写入成功！", refundOrder.getRefundOrderId());
            }

            //TODO 这里应该有一个异步的转账任务，当任务完成时将订单的标志位置改为已完成；可引入消息队列做异步消息处理
            syncTask.doTransferAccount();

            //退款：更新余额
            int remain = userBalance + refundOrder.getRefundAmount();
            int addFlag = walletMapper.updateUserBalance(userId, remain);
            if (addFlag > 0) {
                log.debug("用户{}的订单{}退款成功！", userId, refundOrderId);
            }

            //插入记录至钱包余额明细表
            String walletId = walletMapper.queryUserWalletId(userId);
            if (StringUtils.isEmpty(walletId)) {
                log.error("订单{}:在完成插入余额记录表时异常!", refundOrderId);
                return Response.error("退款失败！");
            }
            WalletBalanceRecord record = new WalletBalanceRecord(userId, walletId, refundOrder.getRefundAmount(), ENTRY_ADD, refundOrderId, nowTime);
            int updateFlag = walletMapper.insertBalanceRecord(record);
            if (updateFlag > 0) {
                log.info("用户{}的订单{}明细记录完成", userId, refundOrderId);
            }
        } catch (InterruptedException e) {
            log.error("获取锁异常:{}", e.getMessage());
            return Response.error("获取锁异常:" + e.getMessage());
        } finally {
            //释放锁
            lock.unlock();
        }
        return Response.ok("退款成功！");
    }

    /**
     * 根据用户id查询钱包余额明细
     *
     * @param queryForm 查询条件：用户id、分页参数:pageNo 0开始
     * @return 余额变动明细列表
     */
    @Override
    public List<WalletBalanceRecord> queryWalletBalanceRecord(QueryForm queryForm) {
        List<WalletBalanceRecord> resultList;
        int total = walletMapper.countWalletBalanceRecord(queryForm.getUserId());
        if (total < 1) {
            return new ArrayList<>();
        }
        Page page = queryForm.getPage();
        page.setTotalRows(total);
        queryForm.setLimitParam((page.getPageNo() - 1) * page.getPageItems());
        resultList = walletMapper.queryWalletBalanceRecord(queryForm);
        return resultList;
    }


}
