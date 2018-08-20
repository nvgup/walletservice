package com.simplecasino.walletservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

public class BalanceResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    private BigDecimal balance;

    public BalanceResponse(String message, BigDecimal balance) {
        this(balance);
        this.message = message;
    }

    public BalanceResponse(BigDecimal balance) {
        this.balance = balance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}