package com.wko.dothings.wallet.service;


import com.wko.dothings.common.base.Response;
import com.wko.dothings.wallet.entities.*;

import java.util.List;

public interface WalletService {

    int queryUserWalletBalance(String userId);

    Response userSmartPay(PurchaseOrder purchaseOrder);

    Response userRefund(RefundOrderVO refundOrderVO);

    List<WalletBalanceRecord> queryWalletBalanceRecord(QueryForm queryForm);

}
