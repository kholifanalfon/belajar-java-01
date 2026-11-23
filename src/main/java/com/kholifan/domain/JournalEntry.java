package com.kholifan.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class JournalEntry {

    private final String number;
    private final LocalDate date;
    private final String description;
    private final List<JournalLine> lines = new ArrayList<>();

    public JournalEntry(String number, LocalDate date, String description) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Nomor jurnal wajib diisi");
        }
        this.number = number;
        this.date = Objects.requireNonNull(date, "Tanggal jurnal wajib diisi");
        this.description = description;
    }

    public JournalEntry addLine(JournalLine line) {
        lines.add(Objects.requireNonNull(line, "Barin jurnal tidak boleh null"));
        return this;
    }

    public JournalEntry debit(String accountCode, BigDecimal amount) {
        return addLine(JournalLine.debit(accountCode, amount));
    }

    public JournalEntry credit(String accountCode, BigDecimal amount) {
        return addLine(JournalLine.credit(accountCode, amount));
    }

    public BigDecimal totalDebit() {
        return sum(JournalLine::debit);
    }

    public BigDecimal totalCredit() {
        return sum(JournalLine::credit);
    }

    public BigDecimal difference() {
        return totalDebit().subtract(totalCredit());
    }

    public boolean isBalanced() {
        return lines.size() >= 2
                && totalDebit().signum() > 0
                && totalDebit().compareTo(totalCredit()) == 0;
    }

    public void ensureBalanced() {
        if (isBalanced())
            throw new UnbalancedJournalException(number, totalDebit(), totalCredit());
    }

    public BigDecimal sum(Function<JournalLine, BigDecimal> side) {
        return lines.stream()
                .map(side)
                .reduce(Money.ZERO, BigDecimal::add);
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public List<JournalLine> getLines() {
        return lines;
    }
}
