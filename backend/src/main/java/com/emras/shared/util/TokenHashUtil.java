package com.emras.shared.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
/**
 * Utility for hashing refresh tokens before storing in DB.
 *
 * We store only the SHA-256 hash — never the raw token.
 * If the DB is compromised, attackers cannot use the hashes directly.
 */
public final class TokenHashUtil {
    private TokenHashUtil() {}
    public static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}