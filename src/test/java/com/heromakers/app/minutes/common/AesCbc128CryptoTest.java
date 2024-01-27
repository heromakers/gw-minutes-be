package com.heromakers.app.minutes.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AesCbc128CryptoTest {

    @Test
    void crypto() {
        try {
            String encrypted = AesCbc128Crypto.getInstance().encrypt("admin");
            System.out.println("encrypted: " + encrypted);
            String decrypted = AesCbc128Crypto.getInstance().decrypt(encrypted);
            assertEquals("admin", decrypted);

            System.out.println("manager : " + AesCbc128Crypto.getInstance().encrypt("manager"));
            System.out.println("user : " + AesCbc128Crypto.getInstance().encrypt("user"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}