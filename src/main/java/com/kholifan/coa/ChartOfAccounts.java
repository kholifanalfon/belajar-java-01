package com.kholifan.coa;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.kholifan.domain.Account;
import com.kholifan.domain.AccountType;
import com.kholifan.domain.JournalEntry;
import com.kholifan.domain.JournalLine;

public class ChartOfAccounts {

    private final List<Account> accounts = new ArrayList<>();
    private final Set<String> codes = new HashSet<>();
    private final Map<String, Account> accountsByCode = new HashMap<>();

    public Account add(Account account) {
        if (!codes.add(account.getCode())) {
            throw new DuplicateAccountCodeException(account.getCode());
        }
        accounts.add(account);
        accountsByCode.put(account.getCode(), account);
        return account;
    }

    public boolean tryAdd(Account account) {
        if (codes.contains(account.getCode())) {
            return false;
        }

        add(account);
        return true;
    }

    public boolean contains(String code) {
        return codes.contains(code);
    }

    public Optional<Account> findByCode(String code) {
        return Optional.ofNullable(accountsByCode.get(code));
    }

    public Account getByCode(String code) {
        return findByCode(code).orElseThrow(() -> new AccountNotFoundException(code));
    }

    public List<Account> findAll() {
        return List.copyOf(accounts);
    }

    public int size() {
        return accounts.size();
    }

    public List<Account> findActive() {
        return accounts.stream()
                .filter(Account::isActive)
                .toList();
    }

    public List<String> findActiveNames() {
        return accounts.stream()
                .filter(Account::isActive)
                .map(Account::getName)
                .toList();
    }

    public List<Account> findByType(AccountType type) {
        return accounts.stream()
                .filter(account -> account.getType() == type)
                .toList();
    }

    public List<Account> searchActiveByName(String keyword) {
        String lower = keyword.toLowerCase();
        return accounts.stream()
                .filter(Account::isActive)
                .filter(account -> account.getName().toLowerCase().contains(lower))
                .toList();
    }

    public Map<AccountType, List<Account>> groupByType() {
        return accounts.stream()
                .collect(Collectors.groupingBy(
                        Account::getType,
                        () -> new EnumMap<>(AccountType.class),
                        Collectors.toList()));
    }

    public void validate(JournalEntry entry) {
        for (JournalLine line : entry.getLines()) {
            Account account = getByCode(line.accountCode());

            if (!account.isActive()) {
                throw new IllegalStateException("Akun %s (%s) nonaktif".formatted(account.getCode()));
            }
        }
        entry.ensureBalanced();
    }

    public void post(JournalEntry entry) {
        validate(entry);

        for (JournalLine line : entry.getLines()) {
            Account account = getByCode(line.accountCode());
            if (line.isDebit()) {
                account.debit(line.debit());
            } else {
                account.credit(line.credit());
            }
        }
    }
}
