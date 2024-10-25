package org.example.utils;

import java.util.UUID;

public class TransactionLogger {
    private static final ThreadLocal<String> transactionIdHolder = new ThreadLocal<>();

    public static String generateTransactionId() {
        String transactionId = UUID.randomUUID().toString();
        transactionIdHolder.set(transactionId);
        return transactionId;
    }

    public static String getTransactionId() {
        return generateTransactionId();
    }

    public static void clear() {
        transactionIdHolder.remove();
    }
}
