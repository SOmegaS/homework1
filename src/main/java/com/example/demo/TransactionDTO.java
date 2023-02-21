package com.example.demo;

import lombok.Getter;

import java.math.BigInteger;

public class TransactionDTO {
    @Getter
    private final BigInteger id;

    @Getter
    private final BigInteger sender;

    @Getter
    private final BigInteger receiver;

    @Getter
    private volatile boolean cancelled = false;

    @Getter
    private final double money;

    public TransactionDTO(BigInteger id, BigInteger sender, BigInteger receiver, double money) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.money = money;
    }

    public void cancel() {
        cancelled = true;
    }
}
