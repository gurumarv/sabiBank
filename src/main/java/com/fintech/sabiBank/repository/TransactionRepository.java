package com.fintech.sabiBank.repository;

import com.fintech.sabiBank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
