package com.kholifan.coa;

public class DuplicateAccountCodeException extends RuntimeException {

    public DuplicateAccountCodeException(String code) {
        super("Kode akun %s sudah terdaftar".formatted(code));
    }
}
