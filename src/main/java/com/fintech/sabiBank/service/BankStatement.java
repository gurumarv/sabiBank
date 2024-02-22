package com.fintech.sabiBank.service;

import com.fintech.sabiBank.dto.EmailDetails;
import com.fintech.sabiBank.model.Transaction;
import com.fintech.sabiBank.model.User;
import com.fintech.sabiBank.repository.TransactionRepository;
import com.fintech.sabiBank.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private static final String FILE = "C:\\Users\\User\\Desktop\\Bank Statement\\BankStatement.pdf";


    public List<Transaction> generateStatement(String accountNumber, String startDate, String stopDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate stop = LocalDate.parse(stopDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(ChronoLocalDate.from(start.atStartOfDay())))
                .filter(transaction -> transaction.getCreatedAt().isEqual(ChronoLocalDate.from(stop.atStartOfDay()))).toList();

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName();

        Document document = new Document();
        document.setPageSize(PageSize.A4);
        log.info("Setting the size of the document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("sabiBank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.CYAN);
        bankName.setPadding(20f);
        PdfPCell bankAddress = new PdfPCell(new Phrase("Lagos, Nigeria"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell startDateInfo = new PdfPCell(new Phrase("Start Date: " + startDate));
        startDateInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell endDateInfo = new PdfPCell(new Phrase("Start Date: " + stopDate));
        endDateInfo.setBorder(0);
        PdfPCell customerNameInfo = new PdfPCell(new Phrase("Customer Name: " + customerName));
        customerNameInfo.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell customerAddressInfo = new PdfPCell(new Phrase("Address: " + user.getAddress()));
        customerAddressInfo.setBorder(0);
        statementInfo.addCell(startDateInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDateInfo);
        statementInfo.addCell(customerNameInfo);
        statementInfo.addCell(space);
        statementInfo.addCell(customerAddressInfo);

        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.CYAN);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.CYAN);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.CYAN);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.CYAN);
        status.setBorder(0);
        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionsTable.addCell(transaction.getCreatedAt().toString());
            transactionsTable.addCell(transaction.getTransactionType());
            transactionsTable.addCell(transaction.getAmount().toString());
            transactionsTable.addCell(transaction.getStatus());});

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Statement of Account")
                .messageBody("Kindly find your requested account statement attached to this email")
                .attachment(FILE)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);
        document.close();




        return transactionList;
    }

}
