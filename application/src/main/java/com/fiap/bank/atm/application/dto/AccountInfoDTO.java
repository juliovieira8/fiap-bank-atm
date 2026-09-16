package com.fiap.bank.atm.application.dto;

import java.math.BigDecimal;

public record AccountInfoDTO(
    String id,
    String agency,
    String number,
    BigDecimal balance,
    String status
) {}
