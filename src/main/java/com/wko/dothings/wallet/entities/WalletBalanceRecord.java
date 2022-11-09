package com.wko.dothings.wallet.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletBalanceRecord {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 钱包id
     */
    private String walletId;

    /**
     * 余额变动数（单位：元）
     */
    private int balanceChange;

    /**
     * 余额是增还是减：0：减  1：增
     */
    private String isEntry;

    /**
     * 关联的订单id
     */
    private Long orderId;

    /**
     * 创建时间(也是关联订单完成时间)
     */
    private String createTime;

}
