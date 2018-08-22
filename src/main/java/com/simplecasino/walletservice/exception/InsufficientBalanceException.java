package com.simplecasino.walletservice.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RestApiException {

    private BigDecimal balance;

    public InsufficientBalanceException(Type type, BigDecimal balance) {
        super(type);
        this.balance = balance;
    }

    public InsufficientBalanceException(Type type, String message, BigDecimal balance) {
        super(type, message);
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}