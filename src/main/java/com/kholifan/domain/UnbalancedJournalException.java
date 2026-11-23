package com.kholifan.domain;

import java.math.BigDecimal;

public class UnbalancedJournalException extends RuntimeException {
    private final BigDecimal difference;

    public UnbalancedJournalException(String journalNumber, BigDecimal totalDebit, BigDecimal totalCredit) {
        super("Jurnal %s tidak seimbang: debit %s, kredit %s, selisih %s".formatted(journalNumber,
                Money.format(totalDebit), Money.format(totalCredit), Money.format(totalDebit.subtract(totalCredit))));

        this.difference = totalDebit.subtract(totalCredit);
    }

    public BigDecimal getDifference() {
        return this.difference;
    }
}
