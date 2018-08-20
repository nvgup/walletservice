package com.simplecasino.walletservice;

import java.math.BigDecimal;

public class UpdateBalanceRequest {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
