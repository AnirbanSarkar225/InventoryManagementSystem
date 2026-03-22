package com.inventory.util;

import com.inventory.model.Product;
import com.inventory.model.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ReportExporter() {}

    public static void exportProductsToCSV(List<Product> products, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Name,Category,Quantity,Price,Supplier,Expiry Date,Reorder Level,Status");
            for (Product p : products) {
                writer.printf("%d,%s,%s,%d,%.2f,%s,%s,%d,%s%n",
                        p.getId(),
                        escapeCsv(p.getName()),
                        escapeCsv(p.getCategory()),
                        p.getQuantity(),
                        p.getPrice(),
                        escapeCsv(p.getSupplier()),
                        p.getExpiryDate() != null ? p.getExpiryDate().toString() : "N/A",
                        p.getReorderLevel(),
                        p.isLowStock() ? "LOW STOCK" : "OK");
            }
        }
    }

    public static void exportTransactionsToCSV(List<Transaction> transactions, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Product,Type,Quantity,Unit Price,Total Value,Timestamp,Remarks,Performed By");
            for (Transaction t : transactions) {
                writer.printf("%d,%s,%s,%d,%.2f,%.2f,%s,%s,%s%n",
                        t.getId(),
                        escapeCsv(t.getProductName()),
                        t.getType(),
                        t.getQuantity(),
                        t.getUnitPrice(),
                        t.getTotalValue(),
                        t.getTimestamp() != null ? t.getTimestamp().format(FORMATTER) : "",
                        escapeCsv(t.getRemarks()),
                        escapeCsv(t.getPerformedBy()));
            }
        }
    }

    public static void exportInventorySummaryToTxt(List<Product> products, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("=========================================");
            writer.println("       INVENTORY MANAGEMENT SYSTEM       ");
            writer.println("         INVENTORY SUMMARY REPORT        ");
            writer.println("=========================================");
            writer.println("Generated: " + LocalDateTime.now().format(FORMATTER));
            writer.println();

            int totalProducts = products.size();
            int totalUnits = products.stream().mapToInt(Product::getQuantity).sum();
            double totalValue = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
            long lowStockCount = products.stream().filter(Product::isLowStock).count();
            long expiredCount = products.stream().filter(Product::isExpired).count();

            writer.printf("Total Products    : %d%n", totalProducts);
            writer.printf("Total Units       : %d%n", totalUnits);
            writer.printf("Total Value       : INR %.2f%n", totalValue);
            writer.printf("Low Stock Items   : %d%n", lowStockCount);
            writer.printf("Expired Items     : %d%n", expiredCount);
            writer.println();
            writer.println("-----------------------------------------");
            writer.printf("%-5s %-25s %-15s %8s %10s%n", "ID", "Name", "Category", "Qty", "Price");
            writer.println("-----------------------------------------");

            for (Product p : products) {
                writer.printf("%-5d %-25s %-15s %8d %10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }

            writer.println("=========================================");
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
