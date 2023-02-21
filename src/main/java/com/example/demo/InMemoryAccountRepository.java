package com.example.demo;

import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryAccountRepository {
    public static Map<BigInteger, AccountDTO> accounts = new HashMap<>();
    private static BigInteger id_counter = BigInteger.valueOf(0);
    public static synchronized AccountDTO createAccount(double balance) {
        AccountDTO account = new AccountDTO(id_counter, balance);
        id_counter = id_counter.add(BigInteger.valueOf(1));
        accounts.put(account.getId(), account);
        return account;
    }

    public static synchronized AccountDTO addBalance(BigInteger id, double money) {
        AccountDTO account = accounts.get(id);
        account.setBalance(accounts.get(id).getBalance() + money);
        return account;
    }

    public static synchronized void commitTransaction(TransactionDTO transaction) {
        AccountDTO account1 = accounts.get(transaction.getSender());
        AccountDTO account2 = accounts.get(transaction.getReceiver());
        addBalance(transaction.getSender(), -transaction.getMoney());
        addBalance(transaction.getReceiver(), transaction.getMoney());
        account1.addTransaction(transaction);
        account2.addTransaction(transaction);
    }

    public static synchronized void cancelTransaction(TransactionDTO transaction) {
        AccountDTO account1 = accounts.get(transaction.getSender());
        AccountDTO account2 = accounts.get(transaction.getReceiver());
        addBalance(transaction.getSender(), transaction.getMoney());
        addBalance(transaction.getReceiver(), -transaction.getMoney());
    }

    public static AccountDTO getAccount(BigInteger id) {
        return accounts.get(id);
    }
}
