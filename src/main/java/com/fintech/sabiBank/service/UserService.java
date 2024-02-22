package com.fintech.sabiBank.service;

import com.fintech.sabiBank.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);

    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);

    TransferResponse transfer(TransferRequest request);
    BankResponse login (LoginDto loginDto);

}
