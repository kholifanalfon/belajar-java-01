package com.kholifan.coa;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String code) {
        super("Akun dengan kode %s tidak ditemukan".formatted(code));
    }

}
