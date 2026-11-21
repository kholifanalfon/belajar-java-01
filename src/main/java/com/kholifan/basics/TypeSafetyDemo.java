package com.kholifan.basics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TypeSafetyDemo {
    private TypeSafetyDemo() {
    }

    public static void run() {
        IO.println("\n=== 1. Static Typing & Type Safety ===");

        primitiveVsReference();
        casting();
        compileTimeChecking();
        genericsAndOptional();
    }

    private static void primitiveVsReference() {
        IO.println("--- Privimive vs Reference ---");

        int count = 10; // primitive: nilai langsung, tidak bisa null
        Integer boxed = count; // autoboxing ke reference type
        Integer nothing = null; // reference type bisa null

        IO.println("int count == " + count + ", Integer boxed = " + boxed + ", Integer nothing = " + nothing);

        // Jebakan: == pada reference membandingkan alamat objek, bukan isi
        Integer a = 127, b = 127; // di-cache JVM (-128..127)
        Integer c = 1000, d = 1000; // objek berbeda
        IO.println("127 == 127 -> " + (a == b) + " | 1000 == 1000 -> " + (c == d)
                + "| 1000 equals 1000 -> " + c.equals(d));

        // Jebakan: unboxing null -> NullPointerException saat runtime
        try {
            int value = nothing;
            IO.println("tidak akan tercetak" + value);
        } catch (NullPointerException e) {
            IO.println("Unboxing null -> NullPointerException");
        }

        IO.println();
    }

    private static void casting() {
        IO.println("-- Casting");

        int small = 100;
        long widened = small; // widening: int ke long (aman)
        long big = 3_00_00_00L;
        int narrowed = (int) big; // narrowing: eksplisit dan bisa kehilangan data
        double price = 9.99;
        int truncated = (int) price; // pecahan dibuang, bukan dibulatkan
        IO.println("widening int->long =" + widened);
        IO.println("(int) 3.000.000.000L = " + narrowed + " <-overflow!");
        IO.println("(int) 9.99 = " + truncated + " <- pecahan bilangan");

        // Cast reference yang salah baru gagal saat runtime
        Object something = "1101";
        try {
            Integer number = (Integer) something;
            IO.println("tidak akan tercetak" + number);
        } catch (ClassCastException e) {
            IO.println("(Integer) \"1101\" -> ClassCastException");
        }

        // Pattern matching instanceof: cek tipe + cast sekaligus, aman
        if (something instanceof String code) {
            IO.println("Pattern matching: kode akun = " + code + ", panjang " + code.length());
        }

        IO.println();
    }

    private static void compileTimeChecking() {
        IO.println("-- Compile-time checking");

        // Baris-baris di bawah DITOLAK compiler, sehingga tidak pernah sampai
        // production:
        // int total = "1000" // incompatible types: String cannot be converted to int
        // String message = 1101; // incompatible types: int cannot be converted to
        // String
        // List<String> codes = new ArrayList<Integer>() // incompatible types
        // Bandingkan dengan PHP: $tota = "1000" + 1; tetap jalan dan baru ketahuan saat
        // runtime

        var total = 1_000 + 110; // var: tipe tetap disimpulkan compiler (int)
        IO.println("var total = " + total + " (time tetap int, disimpulkan compiler)");
    }

    private static void genericsAndOptional() {
        IO.println("-- Generics & Optional");

        // Tanpa generics (rwa type) list bisa diisi apa saja, dan error baru muncul
        // saat runtime:
        // List codes = new ArrayList(); codes.add(1101); String c = (String)
        // codes.get(0); // ClassCastException at runtime
        // dengan generics compiler menjamin isinya seragam:

        List<String> codes = new ArrayList<>();
        codes.add("1101");
        codes.add("4101");
        // code.add(2105); // compile error: int cannot be converted to String

        String first = codes.getFirst(); // tidak perlu cast
        IO.println("List<String> codes = " + codes + ", first = " + first);

        // Optional intuk nilai yang mungkin kosong, bukan null
        Optional<String> found = codes.stream().filter(code -> code.startsWith("4")).findFirst();
        Optional<String> missing = codes.stream().filter(code -> code.startsWith("9")).findFirst();
        IO.println("Optional found = " + found.orElse("-") + ", missing = " + missing.orElse("(tidak ada)"));

        IO.println();
    }
}
