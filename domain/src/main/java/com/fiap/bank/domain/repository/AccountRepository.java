package com.fiap.bank.domain.repository;

import com.fiap.bank.domain.model.Account;
import java.util.Optional;

public interface AccountRepository extends ATMRepository<Account> {
    Optional<Account> findByAccountNumber(String accountNumber);
}
