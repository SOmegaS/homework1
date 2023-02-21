package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class AccountDTO {
    @Getter
    private final BigInteger id;

    @Getter
    @Setter
    private volatile double balance;

    @Getter
    private final Map<BigInteger, TransactionDTO> transactions = new HashMap<>();

    public AccountDTO(long id, double balance) {
        this.id = BigInteger.valueOf(id);
        this.balance = balance;
    }

    public void addTransaction(TransactionDTO transaction) {
        transactions.put(transaction.getId(), transaction);
    }
}
