package com.wko.dothings.wallet.controller;

import com.wko.dothings.common.base.Page;
import com.wko.dothings.common.base.Response;
import com.wko.dothings.wallet.entities.*;
import com.wko.dothings.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Objects;

@RestController
@Slf4j
@Validated
@RequestMapping("/wallet")
public class WalletController {

    @Resource
    private WalletService walletService;

    /**
     * 根据用户id查询用户钱包余额
     *
     * @param userId 用户id
     * @return 用户钱包余额
     */
    @GetMapping("/queryBalance")
    public Response queryUserWalletBalance(String userId) {
        if (StringUtils.isBlank(userId)) {
            return Response.error("查询用户id为空！");
        }
        return Response.ok(walletService.queryUserWalletBalance(userId));
    }

    /**
     * 用户根据订单信息进行消费扣款
     *
     * @param purchaseOrder 消费订单信息
     * @return 成功/失败
     */
    @PostMapping("/consume")
    public Response userSmartPay(@RequestBody @Validated PurchaseOrder purchaseOrder) {
        return walletService.userSmartPay(purchaseOrder);
    }

    /**
     * 用户退款
     *
     * @param refundOrderVO 退款订单信息
     * @return 成功/失败
     */
    @PostMapping("refund")
    public Response userRefund(@RequestBody @Validated RefundOrderVO refundOrderVO) {
        return walletService.userRefund(refundOrderVO);
    }

    /**
     * 根据用户id查询钱包余额明细
     *
     * @param queryForm 查询条件：用户id、分页参数
     * @return 余额变动明细列表
     */
    @PostMapping("/record")
    public Response queryWalletBalanceRecord(@RequestBody QueryForm queryForm) {
        if (Objects.isNull(queryForm.getPage())) {
            queryForm.setPage(new Page());
        }
        return Response.ok(walletService.queryWalletBalanceRecord(queryForm), queryForm.getPage());
    }



    /**
     * 校验对象属性值是否为空
     */
    public boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(object) != null && StringUtils.isNotBlank(field.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("字段校验异常！,{}", e.getMessage());
            return false;
        }
        return true;
    }
}
