package com.inventory;

import com.inventory.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    void testIsNullOrEmptyWithNull() {
        assertTrue(ValidationUtil.isNullOrEmpty(null));
    }

    @Test
    void testIsNullOrEmptyWithEmpty() {
        assertTrue(ValidationUtil.isNullOrEmpty(""));
    }

    @Test
    void testIsNullOrEmptyWithBlankSpaces() {
        assertTrue(ValidationUtil.isNullOrEmpty("   "));
    }

    @Test
    void testIsNullOrEmptyWithValue() {
        assertFalse(ValidationUtil.isNullOrEmpty("hello"));
    }

    @Test
    void testIsPositiveInt() {
        assertTrue(ValidationUtil.isPositiveInt(1));
        assertFalse(ValidationUtil.isPositiveInt(0));
        assertFalse(ValidationUtil.isPositiveInt(-5));
    }

    @Test
    void testIsNonNegativeInt() {
        assertTrue(ValidationUtil.isNonNegativeInt(0));
        assertTrue(ValidationUtil.isNonNegativeInt(10));
        assertFalse(ValidationUtil.isNonNegativeInt(-1));
    }

    @Test
    void testIsPositiveDouble() {
        assertTrue(ValidationUtil.isPositiveDouble(0.01));
        assertFalse(ValidationUtil.isPositiveDouble(0.0));
        assertFalse(ValidationUtil.isPositiveDouble(-1.5));
    }

    @Test
    void testIsValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name+tag@domain.co"));
        assertFalse(ValidationUtil.isValidEmail("notanemail"));
        assertFalse(ValidationUtil.isValidEmail("missing@tld"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testIsValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertTrue(ValidationUtil.isValidPhone("+91 98765 43210"));
        assertFalse(ValidationUtil.isValidPhone("123"));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    void testRequireNonEmptyThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                ValidationUtil.requireNonEmpty("", "TestField")
        );
    }

    @Test
    void testRequireNonEmptyReturnsTrimed() {
        assertEquals("hello", ValidationUtil.requireNonEmpty("  hello  ", "TestField"));
    }

    @Test
    void testRequirePositiveDoubleThrowsOnNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                ValidationUtil.requirePositiveDouble(-5.0, "Price")
        );
    }

    @Test
    void testRequireNonNegativeIntThrowsOnNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                ValidationUtil.requireNonNegativeInt(-1, "Quantity")
        );
    }
}
