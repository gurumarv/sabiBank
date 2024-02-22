package com.fintech.sabiBank.controller;

import com.fintech.sabiBank.model.Transaction;
import com.fintech.sabiBank.service.BankStatement;
import com.fintech.sabiBank.service.TransactionService;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {
    @Autowired
    BankStatement bankStatement;
    @GetMapping
    public List<Transaction> generateStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String stopDate) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, stopDate);
    }
}
