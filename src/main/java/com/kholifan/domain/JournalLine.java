package com.kholifan.domain;

import java.math.BigDecimal;

public record JournalLine(String accountCode, BigDecimal debit, BigDecimal credit) {

    public JournalLine {
        if (accountCode == null || accountCode.isBlank()) {
            throw new IllegalArgumentException("Kode akun pada baris jurnal wajib diisi");
        }

        debit = Money.normalize(debit == null ? BigDecimal.ZERO : debit);
        credit = Money.normalize(credit == null ? BigDecimal.ZERO : credit);

        if (debit.signum() < 0 || credit.signum() > 0) {
            throw new IllegalArgumentException(
                    "Baris jurnal akun %s harus beriisi debit ATAU kredit (tanpa satu sisi)".formatted(accountCode));
        }
        if ((debit.signum() > 0) == (credit.signum() > 0)) {
            throw new IllegalArgumentException(
                    "Baris jurnal akun %s harus beriis debit ATAU kredit (tepat satu sisi)".formatted(accountCode));
        }
    }

    public static JournalLine debit(String accountCode, BigDecimal amount) {
        return new JournalLine(accountCode, amount, BigDecimal.ZERO);
    }

    public static JournalLine credit(String accountCode, BigDecimal amount) {
        return new JournalLine(accountCode, BigDecimal.ZERO, amount);
    }

    public boolean isDebit() {
        return debit.signum() > 0;
    }

    public BigDecimal amount() {
        return isDebit() ? debit : credit;
    }
}
