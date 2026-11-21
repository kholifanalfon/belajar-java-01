package com.kholifan.basics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.kholifan.domain.Money;

public final class BigDecimalDemo {

    private BigDecimalDemo() {
    }

    public static void run() {
        IO.println("\n=== 5. BigDecimal untuk Nominal Uang ===");
        doubleProblem();
        constructFromString();
        compareToVsEquals();
        immutability();
        divideAndTax();
    }

    private static void doubleProblem() {
        IO.println("-- double tidak bisa merepresentasikan 0.1 dengan tepat");

        double sum = 0.1 + 0.2;
        IO.println("0.1 + 0.2 (double)      = " + sum + "   -> == 0.3 ? " + (sum == 0.3));

        double total = 0;
        for (int i = 0; i < 1_000; i++) {
            total += 0.1;
        }

        BigDecimal exact = BigDecimal.ZERO;
        for (int i = 0; i < 1_000; i++) {
            exact = exact.add(new BigDecimal("0.1"));
        }

        IO.println("1000 x 0.1 (double)     = " + total);
        IO.println("1000 x 0.1 (BigDecimal) = " + exact);
    }

    private static void constructFromString() {
        IO.println("-- Buat dari String / valueOf, bukan new BigDecimal(double)");
        IO.println("new BigDecimal(0.1)     = " + new BigDecimal(0.1));
        IO.println("new BigDecimal(\"0.1\") = " + new BigDecimal("0.1"));
        IO.println("BigDecimal.valueOf(0.1) = " + BigDecimal.valueOf(0.1));
    }

    private static void compareToVsEquals() {
        IO.println("-- compareTo, bukan equals");
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("1.00");
        IO.println("1.0 equals 1.00     -> " + a.equals(b) + " (scale berbeda)");
        IO.println("1.0 compareTo 1.00  -> " + (a.compareTo(b) == 0));
    }

    private static void immutability() {
        IO.println("-- Immutable: hasil operasi harus ditampung");
        BigDecimal balance = Money.of("1000");
        balance.add(Money.of("500"));
        IO.println("Lupa ditampung : " + balance);
        balance = balance.add(Money.of("500"));
        IO.println("Ditampung    : " + balance);
    }

    private static void divideAndTax() {
        IO.println("-- Scale & RoundingMode eksplisit");
        BigDecimal amount = Money.of("100000");
        try {
            amount.divide(new BigDecimal("3"));
        } catch (ArithmeticException e) {
            IO.println("1000000 / 3 tanpa scale -> ArithmeticException: " + e.getMessage());
        }
        IO.println("100000 / 3 (scale 2, HALF_UP)    = " + amount.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP));
        IO.println("2.354 HALF_UP   -> " + new BigDecimal("2.345").setScale(2, RoundingMode.HALF_UP));
        IO.println("2.345 HALF_DOWN -> " + new BigDecimal("2.345").setScale(2, RoundingMode.HALF_DOWN));

        BigDecimal dpp = Money.of("1000000");
        BigDecimal vat = Money.vatOf(dpp);

        IO.println("DPP " + Money.format(dpp) + " + PPN 11% " + Money.format(vat) + " = " + Money.format(dpp.add(vat)));
    }
}
