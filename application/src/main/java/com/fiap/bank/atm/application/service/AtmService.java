package com.fiap.bank.atm.application.service;

import com.fiap.bank.atm.application.dto.AccountInfoDTO;
import com.fiap.bank.atm.application.dto.TransactionDTO;
import com.fiap.bank.atm.domain.exception.InvalidPinException;
import com.fiap.bank.atm.domain.model.Account;
import com.fiap.bank.atm.domain.model.Money;
import com.fiap.bank.atm.domain.model.Transaction;
import com.fiap.bank.atm.domain.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class AtmService {
    private final AccountRepository accountRepository;
    private Account currentAccount;

    public AtmService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Retorna o DTO em vez da Entidade
    public AccountInfoDTO authenticate(String accountNumber, String pin) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new InvalidPinException("Conta não encontrada.");
        }

        try {
            account.authenticate(pin);
            currentAccount = account;
            return toAccountDTO(account);
        } catch (RuntimeException e) {
            accountRepository.save(account); // Salva a falha
            throw e;
        }
    }

    public void withdraw(double amount) {
        ensureAuthenticated();
        currentAccount.withdraw(Money.of(amount));
        accountRepository.save(currentAccount);
    }

    public void deposit(double amount) {
        ensureAuthenticated();
        currentAccount.deposit(Money.of(amount));
        accountRepository.save(currentAccount);
    }

    public void transfer(String targetAccountNumber, double amount) {
        ensureAuthenticated();

        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
        if (targetAccount == null) {
            throw new IllegalArgumentException("Conta de destino não encontrada.");
        }
        currentAccount.transfer(targetAccount, Money.of(amount));

        accountRepository.save(currentAccount);
        accountRepository.save(targetAccount);
    }

    // Retorna BigDecimal em vez do objeto de Domínio Money
    public BigDecimal getBalance() {
        ensureAuthenticated();
        return currentAccount.getBalance().getAmount(); 
    }

    // Retorna uma Lista de DTOs mapeados
    public List<TransactionDTO> getStatement() {
        ensureAuthenticated();
        return currentAccount.getTransactions().stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    public void logout() {
        currentAccount = null;
    }

    // Retorna o DTO do usuário atual
    public AccountInfoDTO getCurrentAccount() {
        if (currentAccount == null) return null;
        return toAccountDTO(currentAccount);
    }

    public boolean isAuthenticated() {
        return currentAccount != null;
    }

    private void ensureAuthenticated() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário está autenticado no momento.");
        }
    }

    // --- MÉTODOS TRADUTORES (MAPPERS) PRIVADOS ---

    private AccountInfoDTO toAccountDTO(Account account) {
    return new AccountInfoDTO(
        account.getId() != null ? account.getId().toString() : null,
        "0001", // Valor padrão adicionado pois não existe getAgency() no original
        account.getAccountNumber(),                                  
        account.getBalance().getAmount(),                            
        "ACTIVE" // Valor padrão adicionado pois não existe getStatus() no original
    );
}

    private TransactionDTO toTransactionDTO(Transaction tx) {
        return new TransactionDTO(
            tx.getId() != null ? tx.getId().toString() : null,
            currentAccount.getId() != null ? currentAccount.getId().toString() : null,
            tx.getType() != null ? tx.getType().toString() : "",
            tx.getAmount().getAmount(),
            tx.getCreatedAt()
        );
    }
}