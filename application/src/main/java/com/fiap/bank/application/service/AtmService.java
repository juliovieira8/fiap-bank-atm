package com.fiap.bank.atm.application.service;

import com.fiap.bank.atm.application.dto.AccountInfoDTO;
import com.fiap.bank.atm.application.dto.TransactionDTO;
import com.fiap.bank.atm.domain.exception.InvalidPinException;
import com.fiap.bank.atm.domain.model.Account;
import com.fiap.bank.atm.domain.model.Money;
import com.fiap.bank.atm.domain.repository.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AtmService {

    private final AccountRepository accountRepository;
    private Account currentAccount;

    public AtmService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountInfoDTO authenticate(String accountNumber, String pin) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new InvalidPinException("Conta não encontrada.");
        }

        try {
            account.authenticate(pin);
            currentAccount = account;
            return toDto(currentAccount); 
        } catch (RuntimeException e) {
            accountRepository.save(account); 
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

    public double getBalance() {
        ensureAuthenticated();
        // Converte de BigDecimal para double
        return currentAccount.getBalance().getAmount().doubleValue(); 
    }

    public List<TransactionDTO> getStatement() {
        ensureAuthenticated();
        return currentAccount.getTransactions().stream()
                .map(t -> new TransactionDTO(
                        t.getType().name(), 
                        t.getAmount().getAmount().doubleValue(), // Converte de BigDecimal para double
                        t.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public void logout() {
        currentAccount = null;
    }

    public AccountInfoDTO getCurrentAccount() {
        if (currentAccount == null) return null;
        return toDto(currentAccount);
    }

    public boolean isAuthenticated() {
        return currentAccount != null;
    }

    private void ensureAuthenticated() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário está autenticado no momento.");
        }
    }

    // --- Método utilitário privado para converter Entidade -> DTO ---
    private AccountInfoDTO toDto(Account account) {
        return new AccountInfoDTO(
                account.getAccountNumber(),
                "", // Como não existe getOwnerName() no domínio, passamos vazio para o DTO
                account.getBalance().getAmount().doubleValue() // Converte de BigDecimal para double
        );
    }
}