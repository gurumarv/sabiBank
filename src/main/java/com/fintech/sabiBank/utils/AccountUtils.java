package com.fintech.sabiBank.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXIST_MESSAGE = "This user already has an account created";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Your account has been successfully created";

    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the Account Number provided does not Exist";
    public static final String ACCOUNT_FOUND_CODE = "005";
    public static final String ACCOUNT_FOUND_MESSAGE = "User Account Found";
    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "006";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account has been credited successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "007";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "008";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been debited successfully ";
    public static final String TRANSFER_SUCCESS_CODE = "009";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Transfer Successful";
    public static final String RECIPIENT_ACCOUNT_NOT_EXIST_CODE = "010";
    public static final String RECIPIENT_ACCOUNT_NOT_EXIST_MESSAGE = "Recipient Account Number does not exist";
    public static final String SENDER_ACCOUNT_NOT_EXIST_CODE = "011";
    public static final String SENDER_ACCOUNT_NOT_EXIST_MESSAGE = "Sender Account Number does not exist";










    public static String generateAccountNumber(){
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int randNumber = (int)Math.floor(Math.random() * (max - min + 1) + min);
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        return year + randomNumber;

    }


}
