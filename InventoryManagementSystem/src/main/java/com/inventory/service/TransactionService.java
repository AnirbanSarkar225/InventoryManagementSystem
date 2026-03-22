package com.inventory.service;

import com.inventory.dao.TransactionDAO;
import com.inventory.dao.TransactionDAOImpl;
import com.inventory.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionService {

    private final TransactionDAO transactionDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAOImpl();
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public Optional<Transaction> getTransactionById(int id) {
        return transactionDAO.getTransactionById(id);
    }

    public List<Transaction> getTransactionsByProduct(int productId) {
        return transactionDAO.getTransactionsByProduct(productId);
    }

    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) {
        return transactionDAO.getTransactionsByType(type);
    }

    public List<Transaction> getTransactionsBetween(LocalDateTime from, LocalDateTime to) {
        return transactionDAO.getTransactionsBetween(from, to);
    }

    public List<Transaction> getRecentTransactions(int limit) {
        return transactionDAO.getRecentTransactions(limit);
    }

    public double getTotalStockInValue(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.STOCK_IN)
                .mapToDouble(Transaction::getTotalValue)
                .sum();
    }

    public double getTotalStockOutValue(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.STOCK_OUT)
                .mapToDouble(Transaction::getTotalValue)
                .sum();
    }
}
