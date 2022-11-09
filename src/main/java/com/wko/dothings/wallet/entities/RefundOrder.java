package com.wko.dothings.wallet.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefundOrder {


    /**
     * 退款订单id
     */
    private Long refundOrderId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 退款金额
     */
    private Integer refundAmount;


    /**
     * 订单id
     */
    private Long orderId;


    /**
     * 退款是否完成：0 未完成，1 已取消，2 已完成退款
     */
    private String isSuccess;

    /**
     * 退款订单创建时间
     */
    private String createTime;

    /**
     * 退款完成时间
     */
    private String endTime;


}
