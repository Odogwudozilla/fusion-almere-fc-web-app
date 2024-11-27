package com.fusionalmerefc.Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusionalmerefc.model.BaseEntity;

/**
 * Utility class providing methods for generating unique, hashed, and external identifiers.
 */
public class UtilityFunctions {

    private static final Logger log = LoggerFactory.getLogger(UtilityFunctions.class);

    /**
     * Generates a combined external identifier from two entities.
     *
     * @param entityA The first entity.
     * @param entityB The second entity.
     * @param <T>     Type of the first entity.
     * @param <U>     Type of the second entity.
     * @return A concatenated identifier in the format "EntityA-EntityB".
     */
    public static <T extends BaseEntity, U extends BaseEntity> String generateCombinedExternalIdentifier(T entityA, U entityB) {
        String result = entityA.getExternalIdentifier() + "-" + entityB.getExternalIdentifier();
        log.info("Generated external identifier: {}", result);
        return result;
    }

    /**
     * Generates a hashed identifier using a random salt and SHA-256 hashing.
     *
     * @param entityA The entity to generate the hashed identifier for.
     * @param <T>     Type of the entity.
     * @return A hashed identifier in the format "hash:salt".
     */
    public static <T extends BaseEntity> String generateHashedIdentifier(T entityA) {
        try {
            String originalIdentifier = entityA.getExternalIdentifier();
            String salt = generateSalt();
            String hash = hashWithSalt(originalIdentifier, salt);
            String result = hash + ":" + salt;
            log.info("Generated hashed identifier: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error generating hashed identifier", e);
            throw new RuntimeException("Failed to generate hashed identifier", e);
        }
    }

    /**
     * Generates a unique identifier based on username and an additional value.
     * The output is Base64 encoded, URL-safe, and limited to 20 characters.
     *
     * @param username   The username.
     * @param otherValue The additional value.
     * @return A unique identifier.
     */
    public static String generateUniqueIdentifier(String username, String otherValue) {
        try {
            String input = username + ":" + otherValue;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String result = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes).substring(0, 20);
            log.info("Generated unique identifier: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error generating unique identifier", e);
            throw new RuntimeException("Error generating unique identifier", e);
        }
    }

    /**
     * Generates a short identifier by combining a hash of the username and a UUID.
     *
     * @param username The username.
     * @return A short unique identifier.
     */
    public static String generateShortIdentifier(String username, int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String hashPrefix = Integer.toHexString(username.hashCode());
        String result = (hashPrefix + uuid).substring(0, length);
        log.info("Generated short identifier: {}", result);
        return result;
    }

    /**
     * Generates a readable identifier from a username and additional value.
     *
     * @param username   The username.
     * @param otherValue The additional value.
     * @return A readable identifier with a format like "username-hash".
     */
    public static String generateReadableIdentifier(String username, String otherValue) {
        String slug = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String uniquePart = Integer.toHexString((username + otherValue).hashCode());
        String combined = slug + "-" + uniquePart;

        // Ensure the identifier is exactly 20 characters long
        if (combined.length() < 20) {
            combined = String.format("%-20s", combined).replace(' ', 'x'); // Pad with 'x' to fill 20 characters
        } else if (combined.length() > 20) {
            combined = combined.substring(0, 20); // Truncate if longer than 20 characters
        }

        log.info("Generated readable identifier: {}", combined);
        return combined;
    }


    /**
     * Decodes the original identifier from a hashed identifier if it matches.
     *
     * @param hashedIdentifier The hashed identifier in the format "hash:salt".
     * @param originalIdentifier The original identifier to verify against the hash.
     * @return The original identifier if the hash matches.
     */
    public static String decodeOriginalIdentifier(String hashedIdentifier, String originalIdentifier) {
        try {
            String[] parts = hashedIdentifier.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid hashed identifier format");
            }
            String hash = parts[0];
            String salt = parts[1];
            String calculatedHash = hashWithSalt(originalIdentifier, salt);

            if (calculatedHash.equals(hash)) {
                log.info("Decoded identifier successfully: {}", originalIdentifier);
                return originalIdentifier;
            } else {
                throw new IllegalArgumentException("Hash mismatch. Cannot decode.");
            }
        } catch (Exception e) {
            log.error("Error decoding original identifier", e);
            throw new RuntimeException("Failed to decode identifier", e);
        }
    }

    // Helper methods
    public static String hashWithSalt(String input, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String saltedInput = input + salt;
        byte[] hashBytes = digest.digest(saltedInput.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public static String generateSalt() {
        return UUID.randomUUID().toString();
    }
}
