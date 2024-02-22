package com.fintech.sabiBank.service;

import com.fintech.sabiBank.config.JwtTokenProvider;
import com.fintech.sabiBank.dto.*;
import com.fintech.sabiBank.model.Role;
import com.fintech.sabiBank.model.User;
import com.fintech.sabiBank.repository.UserRepository;
import com.fintech.sabiBank.utils.AccountUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
//        if(userRepository.existsByEmail(userRequest.getEmail())){
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
//                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
//                    .accountInfo(null).build();
//        }
        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .phoneNumber(userRequest.getPhoneNumber())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .accountNumber(AccountUtils.generateAccountNumber())
                .role(Role.valueOf("USER"))
                .accountBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();
        User savedUser = userRepository.save(user);
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Creation")
                .messageBody("Congratulations, your account has been successfully created.\nYour Account Details.\n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + ".\n" +
                        "Account Number: " + savedUser.getAccountNumber() + ".\nThank you for banking with us!")
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber()).build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        EmailDetails loginAlert = EmailDetails.builder()
                .subject("Login Notification")
                .recipient(loginDto.getEmail())
                .messageBody("You just logged into your account. If this was not you, please contact your bank.")
                .build();
        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Successful")
                .responseMessage(jwtTokenProvider.generateToken(authentication)).build();

    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .accountNumber(foundUser.getAccountNumber()).build()).build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User toCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        toCredit.setAccountBalance(toCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(toCredit);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("Credit Alert")
                .recipient(toCredit.getEmail())
                .messageBody("Dear " + toCredit.getFirstName() + " " + toCredit.getLastName()+ ", you have have been credit.\n"
                + "Amount: " + request.getAmount() + ".\n"
                + "Account Balance: " + toCredit.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(toCredit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("Credit")
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(toCredit.getFirstName() + " " + toCredit.getLastName())
                        .accountNumber(toCredit.getAccountNumber())
                        .accountBalance(toCredit.getAccountBalance())
                        .build()).build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
       User toDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        double availableBalance = toDebit.getAccountBalance().doubleValue();
        double debitAmount = request.getAmount().doubleValue();
        if(availableBalance < debitAmount){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        toDebit.setAccountBalance(toDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(toDebit);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("Debit Alert")
                .recipient(toDebit.getEmail())
                .messageBody("Dear " + toDebit.getFirstName() + " " + toDebit.getLastName()+ ", you have have been debited.\n"
                        + "Amount: " + request.getAmount() + ".\n"
                        + "Account Balance: " + toDebit.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(toDebit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("Debit")
                .build();
        transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(toDebit.getAccountBalance())
                        .accountNumber(toDebit.getAccountNumber())
                        .accountName(toDebit.getFirstName() + " " + toDebit.getLastName())
                        .accountBalance(toDebit.getAccountBalance()).build())
                .build();
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        Boolean isRecipientAccountExist = userRepository.existsByAccountNumber(request.getRecipient());
        Boolean isSenderAccountExist = userRepository.existsByAccountNumber(request.getSender());
        User toDebit = userRepository.findByAccountNumber(request.getSender());
        if(!isRecipientAccountExist){
            return TransferResponse.builder()
                    .sender(request.getSender())
                    .recipient(request.getRecipient())
                    .responseCode(AccountUtils.RECIPIENT_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.RECIPIENT_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountBalance(toDebit.getAccountBalance()).build();
        }
        if (!isSenderAccountExist){
            return TransferResponse.builder()
                    .sender(request.getSender())
                    .recipient(request.getRecipient())
                    .responseCode(AccountUtils.SENDER_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.SENDER_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountBalance(toDebit.getAccountBalance()).build();
        }
        double availableBalance = toDebit.getAccountBalance().doubleValue();
        double debitAmount = request.getAmount().doubleValue();
        if(availableBalance < debitAmount){
            return TransferResponse.builder()
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .recipient(request.getRecipient())
                    .sender(request.getSender())
                    .accountBalance(toDebit.getAccountBalance()).build();
        }
        toDebit.setAccountBalance(toDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(toDebit);
        User toCredit = userRepository.findByAccountNumber(request.getRecipient());
        toCredit.setAccountBalance(toCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(toCredit);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("Debit Alert")
                .recipient(toDebit.getEmail())
                .messageBody("You have successfully transferred N" + request.getAmount() + " to " + toCredit.getFirstName() + " " + toCredit.getLastName() + "\n"
                + " with Account Number: " + toCredit.getAccountNumber() + ".\nYour Balance is N" + toDebit.getAccountBalance())
                .build();
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("Credit Alert")
                .recipient(toCredit.getEmail())
                .messageBody("Dear " + toCredit.getFirstName() + " " + toCredit.getLastName() + ". You have been credited.\n" +
                        "Amount: N" + request.getAmount() + "\n" +
                                "From: " + toDebit.getFirstName() + " " + toDebit.getLastName()
                         + ".\nYour Account Balance is now N" + toCredit.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);
        emailService.sendEmailAlert(creditAlert);
        TransactionDto transactionCredit = TransactionDto.builder()
                .accountNumber(toCredit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("Credit")
                .build();
        transactionService.saveTransaction(transactionCredit);
        TransactionDto transactionDebit = TransactionDto.builder()
                .accountNumber(toDebit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("Debit")
                .build();
        transactionService.saveTransaction(transactionDebit);
        return TransferResponse.builder()
                .sender(request.getSender())
                .recipient(request.getRecipient())
                .accountBalance(toDebit.getAccountBalance())
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .build();
    }




}
