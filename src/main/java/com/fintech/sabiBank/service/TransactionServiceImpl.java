package com.fintech.sabiBank.service;

import com.fintech.sabiBank.dto.TransactionDto;
import com.fintech.sabiBank.model.Transaction;
import com.fintech.sabiBank.repository.TransactionRepository;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private TransactionRepository transactionRepository;



    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("Successful").build();
        transactionRepository.save(transaction);

    }

}
