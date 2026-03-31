package com.internet.banking.microservice.account.model;

import com.internet.banking.microservice.account.enums.TransactionType;

import java.util.Date;

public class TransactionHistoryModel {

    private Date dateTime;
    private TransactionType type;
    private String cpfCustomer;
    private String origin;
    private String destination;
    private double amount;
}
