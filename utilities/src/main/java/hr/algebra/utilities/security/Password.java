package hr.algebra.utilities.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class Password {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private static final String SEPARATOR = "$";

    private static final SecureRandom RANDOM = new SecureRandom();

    private Password() { }

    public static String hash(String plainPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] hashed = deriveKey(plainPassword, salt);

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

        byte[] actualHash = deriveKey(plainPassword, salt);

        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    private static byte[] deriveKey(String plainPassword, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ALGORITHM + " is not available", e);
        }
        catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Invalid key spec for password hashing", e);
        }
        finally {
            spec.clearPassword();
        }
    }
}
