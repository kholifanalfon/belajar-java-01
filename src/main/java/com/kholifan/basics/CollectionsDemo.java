package com.kholifan.basics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectionsDemo {
    private CollectionsDemo() {
    }

    public static void run() {
        IO.println("\n=== 2. Generics & Collections ===");

    }

    private static void listBasics() {
        IO.readln("--- List: berurutan, boleh duplikat");

        List<String> codes = new ArrayList<>();
        codes.add("1101");
        codes.add("4101");
        codes.add("5101");
        codes.add("1101");
        IO.println("codes = " + codes + ", size = " + codes.size() + ", get(1) = " + codes.get(1) + ", contains 4101 = "
                + codes.contains("4101"));
    }

    private static void setBasics() {
        IO.println("-- Set: otomatis membuang duplikat");
        List<String> codes = List.of("1101", "4101", "5101", "1101");

        Set<String> unique = new HashSet<>(codes);
        Set<String> ordered = new LinkedHashSet<>(codes);

        IO.println("HashSet         = " + unique + " (urutan tidak dijamin)");
        IO.println("LinkedHashSet   = " + ordered + " (urutan input dijaga)");
        IO.println("add(\"1101\") algi -> " + ordered.add("1101") + " <- false berarti duplikat");
    }

    private static void mapBasics() {
        IO.println("-- Map: key -> value");

        Map<String, String> names = new LinkedHashMap<>();
        names.put("1101", "Kas");
        names.put("4101", "Pendapatan penjualan");
        names.put("2105", "PPN Keluar");

        IO.println("get(\"1101\") = " + names.get("1101"));
        IO.println("containsKey(\"999\") = " + names.containsKey("9999"));
        IO.print("getOrDefault(\"9999\") = " + names.getOrDefault("9999", "(tidak ada)"));

        for (var entry : names.entrySet()) {
            IO.println("  " + entry.getKey() + " => " + entry.getValue());
        }

        IO.println("TreeMap (urut key) = " + new TreeMap<>(names));
    }

    private static void immutableCollections() {
        IO.println("-- List.of / Map.of immutable");
        List<String> fixed = List.of("1101", "4101");

        try {
            fixed.add("5501");
        } catch (UnsupportedOperationException e) {
            IO.println("List.of(...).add() -> UnsupportedOperationException");
        }
    }

    private record Item(String code, String type, long amount, boolean active) {
    }

    private static void streamBassics() {
        List<Item> items = List.of(
                new Item("1101", "Aset", 1_110_000, true),
                new Item("1102", "Aset", 500_000, true),
                new Item("4101", "Pendapatan", 1_000_000, true),
                new Item("2105", "Liabilitas", 110_000, true));

        List<String> activeCodes = items.stream()
                .filter(Item::active)
                .map(Item::code)
                .toList();
        IO.println("filter = map (kode aktif).   =" + activeCodes);

        long total = items.stream().mapToLong(Item::amount).sum();
        IO.println("sum amount            =" + total);

        Map<String, List<String>> byType = items.stream()
                .collect(Collectors.groupingBy(Item::type, TreeMap::new,
                        Collectors.mapping(Item::code, Collectors.toList())));
        IO.println("groupingBy type       = " + byType);

        var pipeline = items.stream().filter(item -> {
            IO.println("  memeriksa " + item.code());
            return item.active();
        });
        IO.println("pipeline dibuat, bleum ada yang diperiksa...");
        IO.println("count = " + pipeline.count());
    }
}
