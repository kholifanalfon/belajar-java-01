package com.kholifan.domain;

import java.util.Arrays;

public enum AccountType {
    ASSET('1', "Asset", NormalBalance.DEBIT),
    LIABILITY('2', "Liabilitas", NormalBalance.CREDIT),
    EQUITY('3', "Ekuitas", NormalBalance.CREDIT),
    REVENUE('4', "Pendapatan", NormalBalance.CREDIT),
    EXPENSE('5', "Beban", NormalBalance.DEBIT);

    private final char prefix;
    private final String label;
    private final NormalBalance normalBalance;

    AccountType(char prefix, String label, NormalBalance normalBalance) {
        this.prefix = prefix;
        this.label = label;
        this.normalBalance = normalBalance;
    }

    public static AccountType fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Kode akun wajib diisi");
        }

        char first = code.charAt(0);
        return Arrays.stream(values())
                .filter(type -> type.prefix == first)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prefix kode akun tidak dikenal: " + code));
    }

    public char getPrefix() {
        return prefix;
    }

    public String getLabel() {
        return label;
    }

    public NormalBalance getNormalBalance() {
        return normalBalance;
    }
}
