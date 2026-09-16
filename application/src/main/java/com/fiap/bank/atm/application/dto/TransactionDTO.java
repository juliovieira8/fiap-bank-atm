package com.fiap.bank.atm.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
    String id,
    String accountId,
    String type,
    BigDecimal amount,
    LocalDateTime createdAt
) {}