package com.fintech.sabiBank.controller;

import com.fintech.sabiBank.dto.*;
import com.fintech.sabiBank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "User Account Management endpoints")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create a new User account",
            description = "Creating a new User with a generated account number" )
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 created"
    )
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }
    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
    @GetMapping("/balance")
    @Operation(summary = "Balance Enquiry",
            description = "Checking the Account Balance of a User" )
    @ApiResponse(
            responseCode = "200",
            description = "Http status: 200 OK"
    )
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }

    @GetMapping("/name")
    @Operation(summary = "Name Enquiry",
            description = "Checking the User Name of a given Account Number " )
    @ApiResponse(
            responseCode = "200",
            description = "Http status: 200 OK"
    )
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.nameEnquiry(enquiryRequest);
    }
    @PostMapping("/credit")
    @Operation(summary = "Credit Account",
            description = "Crediting a User with a given Account Number " )
    @ApiResponse(
            responseCode = "200",
            description = "Http status: 200 OK"
    )
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }
    @PostMapping("/debit")
    @Operation(summary = "Debit Account",
            description = "Debiting a User with a given Account Number." )
    @ApiResponse(
            responseCode = "200",
            description = "Http status: 200 OK"
    )
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }
    @PostMapping("/transfer")
    @Operation(summary = "Transfer",
            description = "Transferring an amount from a User to a recipient with a valid account number." )
    @ApiResponse(
            responseCode = "200",
            description = "Http status: 200 OK"
    )
    public TransferResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }
}
