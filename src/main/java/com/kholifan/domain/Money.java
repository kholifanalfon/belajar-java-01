package com.kholifan.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(SCALE, ROUNDING);

    public static final BigDecimal VAT_RATE = new BigDecimal("0.11");

    private static final Locale INDONESIA = Locale.of("id", "ID");

    private Money() {
    }

    public static BigDecimal of(String amount) {
        return normalize(new BigDecimal(amount));
    }

    public static BigDecimal of(long amount) {
        return normalize(BigDecimal.valueOf(amount));
    }

    public static BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Nominal tidak boleh null");
        }

        return amount.setScale(SCALE, ROUNDING);
    }

    public static BigDecimal vatOf(BigDecimal base) {
        return normalize(base.multiply(VAT_RATE));
    }

    public static boolean isEqual(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    public static String format(BigDecimal amount) {
        return String.format(INDONESIA, "Rp%,.2f", amount);
    }
}
