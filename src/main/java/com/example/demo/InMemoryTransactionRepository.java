package com.example.demo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTransactionRepository {
    public static Map<BigInteger, TransactionDTO> transactions = new HashMap<>();
    private static BigInteger id_counter = BigInteger.valueOf(0);

    public static TransactionDTO createTransaction(BigInteger sender, BigInteger receiver, double money) {
        TransactionDTO transaction = new TransactionDTO(id_counter, sender, receiver, money);
        id_counter = id_counter.add(BigInteger.valueOf(1));
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public static TransactionDTO cancelTransaction(BigInteger id) {
        TransactionDTO transaction = transactions.get(id);
        transaction.cancel();
        transactions.put(id, transaction);
        return transaction;
    }

    public static TransactionDTO getTransaction(BigInteger id) {
        return transactions.get(id);
    }
}
