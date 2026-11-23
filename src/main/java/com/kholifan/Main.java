package com.kholifan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.kholifan.basics.BigDecimalDemo;
import com.kholifan.basics.CollectionsDemo;
import com.kholifan.basics.OopDemo;
import com.kholifan.basics.TypeSafetyDemo;
import com.kholifan.coa.AccountNotFoundException;
import com.kholifan.coa.ChartOfAccounts;
import com.kholifan.coa.DuplicateAccountCodeException;
import com.kholifan.domain.Account;
import com.kholifan.domain.AccountType;
import com.kholifan.domain.JournalEntry;
import com.kholifan.domain.JournalLine;
import com.kholifan.domain.Money;
import com.kholifan.domain.UnbalancedJournalException;

public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "all";
        System.out.println("Running in " + mode + " mode");

        if (!mode.equals("hands-on")) {
            TypeSafetyDemo.run();
            CollectionsDemo.run();
            OopDemo.run();
            BigDecimalDemo.run();
        }

        if (!mode.equals("materi")) {
            handsOn();
        }
    }

    public static void handsOn() {
        IO.println("\n==================== HANDS-ON: COA In-Memory ====================");
        ChartOfAccounts coa = seed();

        scenario1SaveAndLookup(coa);
        scenario2RejectDupplicate(coa);
        scenario3CheckJournal(coa);
    }

    private static ChartOfAccounts seed() {
        ChartOfAccounts coa = new ChartOfAccounts();
        coa.add(Account.of("1101", "Kas"));
        coa.add(Account.of("1102", "Bank BCA"));
        coa.add(Account.of("1201", "Piutang Usaha"));
        coa.add(Account.of("2101", "Utang Usaha"));
        coa.add(Account.of("2105", "PPN Keluaran"));
        coa.add(Account.of("3101", "Modal Disetor"));
        coa.add(Account.of("4101", "Pendapatan Penjualan"));
        coa.add(Account.of("5101", "Beban Gaji"));
        coa.add(new Account("5199", "Beban Lain-lain (lama)", AccountType.EXPENSE, false));
        return coa;
    }

    private static void scenario1SaveAndLookup(ChartOfAccounts coa) {
        IO.println("\n--- Skenario 1: simpan akun & lookup berdasarkan kode");
        coa.add(Account.of("5102", "Beban Listrik"));
        IO.println("Total akun: " + coa.size());
        coa.findAll().forEach(account -> IO.println(" " + account));

        IO.println("Lookup 1101 -> " + coa.findByCode("1101").map(Account::getName).orElse("(tidak ada)"));
        IO.println("Lookup 9999 -> " + coa.findByCode("9999").map(Account::getName).orElse("(tidak ada)"));

        IO.println("Akun aktif (filter + map): " + coa.findActiveNames());
        IO.println("Cari 'beban' di akun aktif: "
                + coa.searchActiveByName("beban").stream().map(Account::getCode).toList());

        Map<AccountType, List<Account>> byType = coa.groupByType();
        byType.forEach((type, accounts) -> IO.println("   %-10s, %-8s: %s".formatted(
                type.getLabel(), "(" + type.getNormalBalance() + ")",
                accounts.stream().map(Account::getCode).toList())));
    }

    private static void scenario2RejectDupplicate(ChartOfAccounts coa) {
        IO.println("\n--- Skenario 2: kode akun duplikat ditolak");

        try {
            coa.add(Account.of("1101", "Kas Kecil"));
            IO.println("GAGAL: duplikat seharusnya ditolak");
        } catch (DuplicateAccountCodeException e) {
            IO.println("Ditolak: " + e.getMessage());
        }

        IO.println(
                "tryAdd 1101 -> " + coa.tryAdd(Account.of("1101", "Kas Kecil")) + ", total akun tetap " + coa.size());

        try {
            Account.of("1101", "    ");
        } catch (IllegalArgumentException e) {
            IO.println("Validasi nama: " + e.getMessage());
        }

        try {
            new Account("1103", "Salah tipe", AccountType.REVENUE, true);
        } catch (IllegalArgumentException e) {
            IO.println("Validasi tipe: " + e.getMessage());
        }
    }

    private static void scenario3CheckJournal(ChartOfAccounts coa) {
        IO.println("\n--- Skenario 3: cek jurnal seimbang / tidak seimbang");

        BigDecimal sales = Money.of("1000000");
        BigDecimal vat = Money.vatOf(sales);
        JournalEntry balanced = new JournalEntry("JU-0001", LocalDate.of(2026, 11, 23), "Penjualan tunai + PPN 11%")
                .debit("1101", sales.add(vat))
                .credit("4101", sales)
                .credit("2105", vat);

        print(balanced, coa);
        coa.post(balanced);

        IO.println("Diposting. Saldo: Kas " + Money.format(coa.getByCode("1101").getBalance())
                + ", Pendapatan " + Money.format(coa.getByCode("4101").getBalance())
                + ", PPN Keluaran " + Money.format(coa.getByCode("2105").getBalance()));

        JournalEntry unbalanced = new JournalEntry("JU-0002", LocalDate.of(2026, 11, 23),
                "Penjualan tunai (PPN lupa dicatat)")
                .debit("1101", Money.of("1110000"))
                .credit("4101", Money.of("1000000"));
        print(unbalanced, coa);

        try {
            coa.post(unbalanced);
        } catch (UnbalancedJournalException e) {
            IO.println("Ditolak: " + e.getMessage());
        }

        JournalEntry unknownAccount = new JournalEntry("JU-0003", LocalDate.of(2026, 11, 23), "Akun tidak terdaftar")
                .debit("5999", Money.of("50000"))
                .credit("1101", Money.of("50000"));

        try {
            coa.validate(unknownAccount);
        } catch (AccountNotFoundException e) {
            IO.println("Ditolak: " + e.getMessage());
        }

        JournalEntry inactiveAccount = new JournalEntry("JU-0004", LocalDate.of(2026, 11, 23), "Akun nonaktif")
                .debit("5199", Money.of("50000"))
                .credit("1101", Money.of("50000"));

        try {
            coa.validate(inactiveAccount);
        } catch (IllegalStateException e) {
            IO.println("Ditolak: " + e.getMessage());
        }

        try {
            new JournalLine("1101", Money.of("1000"), Money.of("1000"));
        } catch (IllegalArgumentException e) {
            IO.println("Ditolak: " + e.getMessage());
        }
    }

    private static void print(JournalEntry entry, ChartOfAccounts coa) {
        IO.println("\n%s | %s | %s".formatted(entry.getNumber(), entry.getDate(), entry.getDescription()));
        IO.println("   %-5s %-22s %18s %18s".formatted("Akun", "Nama", "Debit", "Kredit"));

        for (JournalLine line : entry.getLines()) {
            String name = coa.findByCode(line.accountCode()).map(Account::getName).orElse("?");
            IO.println("   %-5s %-22s %18s %18s".formatted(line.accountCode(), name,
                    line.isDebit() ? Money.format(line.debit()) : "",
                    line.isDebit() ? "" : Money.format(line.credit())));
        }

        IO.println("   %-28s %18s %18s".formatted("Total", Money.format(entry.totalCredit()),
                Money.format(entry.totalCredit())));
        IO.println("   Seimbang? " + entry.isBalanced() + " | selisih " + Money.format(entry.difference()));
    }
}
