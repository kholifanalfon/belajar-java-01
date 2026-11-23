package com.kholifan.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public class Account {

    private static final Pattern CODE_PATTERN = Pattern.compile("[1-5]\\d{3}");

    private final String code;
    private final String name;
    private final AccountType type;
    private boolean active;
    private BigDecimal balance;

    public Account(String code, String name, AccountType type, boolean active) {
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Kode akun harus 4 digit diawali 1-5: " + code);
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama akun wajib diisi");
        }

        Objects.requireNonNull(type, "Tipe akun wajib diisi");
        if (AccountType.fromCode(code) != type) {
            throw new IllegalArgumentException("Kode %s tidak sesuai dengan tipe %s".formatted(code, type.getLabel()));
        }

        this.code = code;
        this.name = name.strip();
        this.type = type;
        this.active = active;
        this.balance = Money.ZERO;
    }

    public static Account of(String code, String name) {
        return new Account(code, name, AccountType.fromCode(code), true);
    }

    public void debit(BigDecimal amount) {
        BigDecimal value = requirePositive(amount);
        balance = type.getNormalBalance() == NormalBalance.DEBIT ? balance.add(value) : balance.subtract(value);
    }

    public void credit(BigDecimal amount) {
        BigDecimal value = requirePositive(amount);
        balance = type.getNormalBalance() == NormalBalance.CREDIT ? balance.add(value) : balance.subtract(value);
    }

    public void activate() {
        this.active = true;
    }

    public void deactive() {
        this.active = false;
    }

    private static BigDecimal requirePositive(BigDecimal amount) {
        BigDecimal value = Money.normalize(amount);
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Nominal harus lebih besar dari nol: " + amount);
        }
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return o instanceof Account other && code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "%s %-25s %-10s %s".formatted(code, name, active ? "aktif" : "nonaktif");
    }
}
