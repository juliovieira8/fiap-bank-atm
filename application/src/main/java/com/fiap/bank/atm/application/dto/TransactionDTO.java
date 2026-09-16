package com.fiap.bank.atm.application.dto;

import java.time.LocalDateTime;

public record TransactionDTO(
    String type, 
    double amount, 
    LocalDateTime timestamp
) {}