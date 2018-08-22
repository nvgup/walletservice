package com.simplecasino.walletservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Embeddable
public class Balance {

    @NotNull
    @Column(name = "balance")
    @Digits(integer = 10, fraction = 2)
    @Min(value = 0)
    private BigDecimal amount;

    public Balance() {
        amount = BigDecimal.ZERO;
    }

    public Balance(@NotNull BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}