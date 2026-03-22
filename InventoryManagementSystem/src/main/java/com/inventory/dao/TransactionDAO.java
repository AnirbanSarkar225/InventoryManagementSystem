package com.inventory.dao;

import com.inventory.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    void addTransaction(Transaction transaction);
    Optional<Transaction> getTransactionById(int id);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactionsByProduct(int productId);
    List<Transaction> getTransactionsByType(Transaction.TransactionType type);
    List<Transaction> getTransactionsBetween(LocalDateTime from, LocalDateTime to);
    List<Transaction> getRecentTransactions(int limit);
}
