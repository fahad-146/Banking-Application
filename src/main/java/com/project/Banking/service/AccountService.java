package com.project.Banking.service;

import com.project.Banking.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    void createAccount(Account account);

    List<Account> getAll();

    Optional<Account> findById(Long id);

    void deleteAccount(Long id);
}
