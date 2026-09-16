package com.fiap.bank.domain.repository;

import com.fiap.bank.domain.model.BaseEntity;
import java.util.Optional;
import java.util.UUID;

public interface ATMRepository<T extends BaseEntity> {
    Optional<T> findById(UUID id);
    void save(T entity);
}