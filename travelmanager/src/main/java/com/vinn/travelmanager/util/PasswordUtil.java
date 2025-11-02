package com.vinn.travelmanager.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final String SALT = "travelPlanner2024";

    /**
     * 加密密码
     */
    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            String saltedPassword = password + SALT;
            byte[] hashBytes = md.digest(saltedPassword.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    public static boolean verify(String password, String encryptedPassword) {
        String encrypted = encrypt(password);
        return encrypted.equals(encryptedPassword);
    }
}

