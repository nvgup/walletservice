package com.simplecasino.walletservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Embeddable
public class Balance {

    @NotNull
    @Positive
    @Column(name = "balance")
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
