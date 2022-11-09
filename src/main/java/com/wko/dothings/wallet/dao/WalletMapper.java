package com.wko.dothings.wallet.dao;


import com.wko.dothings.wallet.entities.QueryForm;
import com.wko.dothings.wallet.entities.RefundOrder;
import com.wko.dothings.wallet.entities.WalletBalanceRecord;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface WalletMapper {

    /**
     * 根据用户id查询钱包余额
     *
     * @param userId 用户id
     */
    @Select("SELECT balance FROM user_wallet WHERE user_id = #{userId}")
    int queryUserWalletBalance(String userId);

    /**
     * 根据用户id查询钱包id
     *
     * @param userId 用户id
     */
    @Select("SELECT wallet_id FROM user_wallet WHERE user_id = #{userId}")
    String queryUserWalletId(String userId);

    /**
     * 根据用户id将用户的钱包余额变更为#{remain}
     *
     * @param userId 用户id
     * @param remain 余额
     */
    @Update("UPDATE user_wallet SET balance = #{remain} WHERE user_id= #{userId}")
    int updateUserBalance(@Param("userId") String userId, @Param("remain") Integer remain);

    /**
     * 将购买订单的状态更新为已完成
     *
     * @param orderId 订单id
     * @param nowTime 当前时间
     */
    @Update("UPDATE tb_purchase_order SET is_finish = '1' , finish_time = #{nowTime,jdbcType=TIMESTAMP} WHERE order_id = #{orderId}")
    int completePurchaseOrder(@Param("orderId") Long orderId, @Param("nowTime") String nowTime);

    /**
     * 将退款订单的状态更新为已完成退款
     *
     * @param refundOrderId 退款订单id
     * @param nowTime       当前时间
     */
    @Update("UPDATE tb_refund_order SET is_success = '2' ,end_time = #{nowTime,jdbcType=TIMESTAMP} WHERE refund_order_id = #{refundOrderId}")
    int completeRefundOrder(@Param("refundOrderId") Long refundOrderId, @Param("nowTime") String nowTime);


    /**
     *
     * @param refundOrder 退款订单信息
     */
    @Insert("INSERT INTO tb_refund_order(refund_order_id,user_id,refund_amount,order_id,create_time,is_success) VALUES " +
            "(#{refundOrderId},#{userId},#{refundAmount},#{orderId},#{createTime},#{isSuccess})")
    int insertRefundOrder(RefundOrder refundOrder);

    /**
     * 向余额明细表中添加记录
     *
     * @param record 钱包明细记录
     */
    @Insert("INSERT INTO wallet_balance_record(user_id,wallet_id,balance_change,is_entry,order_id,create_time) " +
            "VALUES(#{userId},#{walletId},#{balanceChange},#{isEntry},#{orderId},#{createTime,jdbcType=TIMESTAMP})")
    int insertBalanceRecord(WalletBalanceRecord record);

    /**
     * 查询钱包余额明细：分页查询
     */
    @Results(id = "detailMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "walletId", column = "wallet_id"),
            @Result(property = "balanceChange", column = "balance_change"),
            @Result(property = "isEntry", column = "is_entry"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "createTime", column = "create_time", jdbcType = JdbcType.TIMESTAMP),
    })
    @Select("SELECT user_id,wallet_id,balance_change,is_entry,order_id,create_time FROM wallet_balance_record " +
            "WHERE user_id = #{userId} ORDER BY create_time LIMIT #{limitParam},#{page.pageItems}")
    List<WalletBalanceRecord> queryWalletBalanceRecord(QueryForm queryForm);

    /**
     * 获取相关用户钱包余额变动次数
     *
     * @param userId 用户id
     * @return 相关用户的余额记录条数
     */
    @Select("SELECT COUNT(*) FROM wallet_balance_record WHERE user_id = #{userId}")
    int countWalletBalanceRecord(String userId);

}
