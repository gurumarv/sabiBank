package com.fintech.sabiBank.service;

import com.fintech.sabiBank.dto.TransactionDto;
import com.fintech.sabiBank.model.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
