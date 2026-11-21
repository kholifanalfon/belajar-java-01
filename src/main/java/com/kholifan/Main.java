package com.kholifan;

import com.kholifan.basics.TypeSafetyDemo;

public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "all";
        System.out.println("Running in " + mode + " mode");

        if (!mode.equals("hands-on")) {
            TypeSafetyDemo.run();
        }
    }
}
