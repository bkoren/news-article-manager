package hr.algebra.utilities.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class Password {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = "$";

    private static final SecureRandom RANDOM = new SecureRandom();

    private Password() { }

    public static String hash(String plainPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] hashed = hashWithSalt(plainPassword, salt);

        return Base64.getEncoder().encodeToString(salt)
                + SEPARATOR
                + Base64.getEncoder().encodeToString(hashed);
    }

    public static boolean verify(String plainPassword, String stored) {
        if (stored == null || !stored.contains(SEPARATOR)) {
            return false;
        }

        String[] parts = stored.split("\\" + SEPARATOR, 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        byte[] actualHash = hashWithSalt(plainPassword, salt);

        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    private static byte[] hashWithSalt(String plainPassword, byte[] salt) {
        try {
            MessageDigest instance = MessageDigest.getInstance(ALGORITHM);
            instance.update(salt);

            return instance.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}