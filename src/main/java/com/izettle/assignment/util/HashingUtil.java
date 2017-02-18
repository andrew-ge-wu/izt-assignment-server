package com.izettle.assignment.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Project: demo
 * Created by andrew on 2017-02-18.
 */
public class HashingUtil {
    private static final int NR_ROUNDS = 1000;

    public static String hashPassword(String salt, String password) throws NoSuchAlgorithmException {
        String toReturn = password;
        for (int i = 0; i < NR_ROUNDS; i++) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((salt + toReturn).getBytes());
            byte byteData[] = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            toReturn = hexString.toString();
        }
        return toReturn;
    }

}
