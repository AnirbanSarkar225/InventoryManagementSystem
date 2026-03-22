package com.inventory;

import com.inventory.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    void testHashIsNotPlainText() {
        String plain = "mypassword";
        String hashed = PasswordUtil.hash(plain);
        assertNotEquals(plain, hashed);
    }

    @Test
    void testHashIsDeterministic() {
        String plain = "mypassword";
        assertEquals(PasswordUtil.hash(plain), PasswordUtil.hash(plain));
    }

    @Test
    void testHashLength() {
        String hashed = PasswordUtil.hash("anypassword");
        assertEquals(64, hashed.length(), "SHA-256 hex should be 64 characters");
    }

    @Test
    void testVerifyCorrectPassword() {
        String plain = "correcthorsebatterystaple";
        String hashed = PasswordUtil.hash(plain);
        assertTrue(PasswordUtil.verify(plain, hashed));
    }

    @Test
    void testVerifyWrongPassword() {
        String plain = "correctpassword";
        String hashed = PasswordUtil.hash(plain);
        assertFalse(PasswordUtil.verify("wrongpassword", hashed));
    }

    @Test
    void testDifferentPasswordsDifferentHashes() {
        String hash1 = PasswordUtil.hash("password1");
        String hash2 = PasswordUtil.hash("password2");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void testKnownAdminHash() {
        String hashed = PasswordUtil.hash("admin");
        assertEquals("8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", hashed);
    }
}
