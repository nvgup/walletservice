package com.simplecasino.walletservice.dto;

import java.math.BigDecimal;

public class UpdateBalanceRequest {

    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}