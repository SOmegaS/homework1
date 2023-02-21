package com.example.demo;

import java.math.BigInteger;
import java.util.Map;

public class OperationManager {
    public static AccountDTO createAccount(double balance) throws Exception {
        if (balance <= 0) {
            throw new Exception("Wrong balance");
        }
        return InMemoryAccountRepository.createAccount(balance);
    }

    public static AccountDTO addBalance(BigInteger id, double balance) throws Exception {
        if ((balance < 0) && (InMemoryAccountRepository.getAccount(id).getBalance() < balance)) {
            throw new Exception("Not enough money");
        }
        return InMemoryAccountRepository.addBalance(id, balance);
    }

    public static synchronized TransactionDTO createTransaction(BigInteger sender, BigInteger receiver, double money) throws Exception {
        if (InMemoryAccountRepository.getAccount(sender).getBalance() < money) {
            throw new Exception("Not enough money");
        }
        if (money < 0) {
            throw new Exception("Not correct sum");
        }
        TransactionDTO transaction = InMemoryTransactionRepository.createTransaction(sender, receiver, money);
        InMemoryAccountRepository.commitTransaction(transaction);
        return transaction;
    }

    public static synchronized TransactionDTO cancelTransaction(BigInteger id) throws Exception {
        TransactionDTO transaction = InMemoryTransactionRepository.getTransaction(id);
        if (transaction.isCancelled()) {
            return transaction;
        }
        InMemoryAccountRepository.cancelTransaction(transaction);
        return InMemoryTransactionRepository.cancelTransaction(id);
    }

    public static AccountDTO getAccount(BigInteger id) {
        return InMemoryAccountRepository.getAccount(id);
    }

    public static TransactionDTO getTransaction(BigInteger id) {
        return InMemoryTransactionRepository.getTransaction(id);
    }

    public static Map<BigInteger, AccountDTO> getListAccounts() {
        return InMemoryAccountRepository.accounts;
    }

    public static Map<BigInteger, TransactionDTO> getListTransactions() {
        return InMemoryTransactionRepository.transactions;
    }
}
