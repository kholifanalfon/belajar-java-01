package com.kholifan.basics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OopDemo {
    private OopDemo() {
    }

    interface Describable {
        String describe();

        default String headline() {
            return "[" + describe() + "]";
        }
    }

    abstract static class BaseAccount implements Describable {
        private final String code;
        private final String name;

        protected BaseAccount(String code, String name) {
            this.code = code;
            this.name = name;
        }

        abstract String normalBalance();

        public String getCode() {
            return code;
        }

        @Override
        public String describe() {
            return code + " " + name + " (saldo normal " + normalBalance() + ")";
        }
    }

    static final class AssetAccount extends BaseAccount {
        AssetAccount(String code, String name) {
            super(code, name);
        }

        @Override
        String normalBalance() {
            return "DEBET";
        }
    }

    static final class RevenueAccount extends BaseAccount {
        RevenueAccount(String code, String name) {
            super(code, name);
        }

        @Override
        String normalBalance() {
            return "KREDIT";
        }
    }

    static final class AccountCodeNoEquals {
        private final String value;

        AccountCodeNoEquals(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    record AccountCode(String value) {
    }

    public static void run() {
        IO.println("\n=== 4. Review OOP ===");
        polymorphism();
        equalsAndHasCode();
    }

    private static void polymorphism() {
        IO.println("-- Polymorphism: List<BaseAccount> berisi Asset & Revenue");
        List<BaseAccount> accounts = List.of(
                new AssetAccount("1101", "Kas"),
                new RevenueAccount("4101", "Pendapatan Penjualan"));

        for (BaseAccount account : accounts) {
            IO.println(" " + account.headline());
        }
    }

    private static void equalsAndHasCode() {
        IO.println("-- equals() & hasCode()");
        String s1 = new String("1101");
        String s2 = new String("1101");
        IO.println("\"1101\" == \"1101\"       -> " + (s1 == s2) + " (beda object)");
        IO.print("\"1101\".equals(\"1101\")    -> " + s1.equals(s2) + " (isi sama)");

        Set<AccountCodeNoEquals> broken = new HashSet<>();
        broken.add(new AccountCodeNoEquals("1101"));
        broken.add(new AccountCodeNoEquals("1101"));
        IO.println("HasSet tanpa equals/hasCode -> size " + broken.size() + " " + broken + "   <- jawaban Q4");

        Set<AccountCode> correct = new HashSet<>();
        correct.add(new AccountCode("1101"));
        correct.add(new AccountCode("1101"));
        IO.println("HashSet dengan record.    -> size " + correct.size() + " " + correct);
    }
}
