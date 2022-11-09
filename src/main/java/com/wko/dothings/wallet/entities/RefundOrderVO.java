package com.wko.dothings.wallet.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefundOrderVO {

    /**
     * 用户id
     */
    @NotBlank(message = "用户id不能为空")
    private String userId;

    /**
     * 退款金额
     */
    @Min(message = "金额不能小于0", value = 0)
    @NotNull(message = "金额不能为空")
    private Integer refundAmount;


    /**
     * 关联订单id
     */
    @NotNull(message = "订单id不能为空")
    private Long orderId;
}
