package com.inventory.util;

public class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    public static boolean isNonNegativeInt(int value) {
        return value >= 0;
    }

    public static boolean isPositiveDouble(double value) {
        return value > 0.0;
    }

    public static boolean isNonNegativeDouble(double value) {
        return value >= 0.0;
    }

    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return phone.matches("^[+]?[0-9\\-\\s]{7,15}$");
    }

    public static String requireNonEmpty(String value, String fieldName) {
        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    public static double requirePositiveDouble(double value, String fieldName) {
        if (!isNonNegativeDouble(value)) {
            throw new IllegalArgumentException(fieldName + " must be non-negative.");
        }
        return value;
    }

    public static int requireNonNegativeInt(int value, String fieldName) {
        if (!isNonNegativeInt(value)) {
            throw new IllegalArgumentException(fieldName + " must be non-negative.");
        }
        return value;
    }
}
