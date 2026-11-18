package com.travixa.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class BdtMoney {
    public static final String CURRENCY_CODE = "BDT";
    private static final int SCALE = 2;

    private final BigDecimal amount;

    private BdtMoney(BigDecimal amount) {
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BdtMoney of(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return new BdtMoney(amount);
    }

    public static BdtMoney of(long amount) {
        return new BdtMoney(BigDecimal.valueOf(amount));
    }

    public static BdtMoney zero() {
        return new BdtMoney(BigDecimal.ZERO);
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    public BdtMoney add(BdtMoney other) {
        return new BdtMoney(this.amount.add(Objects.requireNonNull(other, "other").amount));
    }

    public BdtMoney subtract(BdtMoney other) {
        return new BdtMoney(this.amount.subtract(Objects.requireNonNull(other, "other").amount));
    }

    public BdtMoney multiply(BigDecimal multiplier) {
        return new BdtMoney(this.amount.multiply(multiplier));
    }

    public BdtMoney divide(BigDecimal divisor) {
        return new BdtMoney(this.amount.divide(divisor, SCALE, RoundingMode.HALF_UP));
    }

    public boolean isZero() {
        return BigDecimal.ZERO.compareTo(amount) == 0;
    }

    public String format() {
        return amount.setScale(SCALE, RoundingMode.HALF_UP).toPlainString() + " " + CURRENCY_CODE;
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BdtMoney bdtMoney = (BdtMoney) o;
        return amount.compareTo(bdtMoney.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
