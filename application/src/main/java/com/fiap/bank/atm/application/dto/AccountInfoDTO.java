package com.fiap.bank.atm.application.dto;

public record AccountInfoDTO(
    String accountNumber,
    String ownerName,
    double balance
) {}