package com.fusionalmerefc.backend;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusionalmerefc.Utils.UtilityFunctions;
import com.fusionalmerefc.model.BaseEntity;

import static org.junit.jupiter.api.Assertions.*;

class UtilityFunctionsTest {
    private static final Logger log = LoggerFactory.getLogger(UtilityFunctionsTest.class);

    static class TestEntity extends BaseEntity {
        private final String identifier;

        TestEntity(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String getExternalIdentifier() {
            return identifier;
        }
    }

    @Test
    void testGenerateExternalIdentifier() {
        TestEntity entityA = new TestEntity("EntityA");
        TestEntity entityB = new TestEntity("EntityB");

        String externalIdentifier = UtilityFunctions.generateExternalIdentifier(entityA, entityB);
        log.info("The generated external identifier: {}", externalIdentifier);
        assertEquals("EntityA-EntityB", externalIdentifier);
    }

    @Test
    void testGenerateHashedIdentifier() {
        TestEntity entity = new TestEntity("Entity123");

        String hashedIdentifier = UtilityFunctions.generateHashedIdentifier(entity);
        log.info("The generated hashed identifier: {}", hashedIdentifier);

        assertNotNull(hashedIdentifier);
        assertTrue(hashedIdentifier.contains(":"));

        String[] parts = hashedIdentifier.split(":");
        assertEquals(2, parts.length);
        assertFalse(parts[0].isEmpty());
        assertFalse(parts[1].isEmpty());
    }

    @Test
    void testGenerateUniqueIdentifier() {
        String username = "user123";
        String otherValue = "value456";

        String uniqueIdentifier = UtilityFunctions.generateUniqueIdentifier(username, otherValue);
        log.info("Generated unique identifier: {}", uniqueIdentifier);

        assertNotNull(uniqueIdentifier);
        assertEquals(20, uniqueIdentifier.length());
    }

    @Test
    void testGenerateShortIdentifier() {
        String username = "user123";

        String shortIdentifier = UtilityFunctions.generateShortIdentifier(username);
        log.info("Generated short identifier: {}", shortIdentifier);

        assertNotNull(shortIdentifier);
        assertEquals(20, shortIdentifier.length());
    }

    @Test
    void testGenerateReadableIdentifier() {
        String username = "User_123!";
        String otherValue = "Value@456";

        String readableIdentifier = UtilityFunctions.generateReadableIdentifier(username, otherValue);
        log.info("Generated readable identifier: {}", readableIdentifier);

        assertNotNull(readableIdentifier);
        assertEquals(20, readableIdentifier.length());
        assertTrue(readableIdentifier.matches("[a-z0-9]+-[a-z0-9]+"));
    }

    @Test
    void testDecodeOriginalIdentifier() {
        TestEntity entity = new TestEntity("Entity123");
        String hashedIdentifier = UtilityFunctions.generateHashedIdentifier(entity);
        String originalIdentifier = "Entity123";

        String decodedIdentifier = UtilityFunctions.decodeOriginalIdentifier(hashedIdentifier, originalIdentifier);
        log.info("Hashed identifier: {}", hashedIdentifier);
        log.info("Decoded identifier: {}", decodedIdentifier);

        assertEquals(originalIdentifier, decodedIdentifier);
    }

    @Test
    void testDecodeOriginalIdentifierInvalidFormat() {
        String invalidHashedIdentifier = "InvalidFormat";

        Exception exception = assertThrows(RuntimeException.class, () ->
                UtilityFunctions.decodeOriginalIdentifier(invalidHashedIdentifier, "Entity123"));

        log.error("Error: {}", exception.getMessage());
        assertEquals("Failed to decode identifier", exception.getMessage());
    }

    @Test
    void testDecodeOriginalIdentifierHashMismatch() {
        TestEntity entity = new TestEntity("Entity123");
        String hashedIdentifier = UtilityFunctions.generateHashedIdentifier(entity);
        String differentIdentifier = "Entity456";

        Exception exception = assertThrows(RuntimeException.class, () ->
                UtilityFunctions.decodeOriginalIdentifier(hashedIdentifier, differentIdentifier));

        log.error("Error: {}", exception.getMessage());
        assertEquals("Failed to decode identifier", exception.getMessage());
    }

    @Test
    void testGenerateSalt() {
        String salt1 = UtilityFunctions.generateSalt();
        String salt2 = UtilityFunctions.generateSalt();

        log.info("Generated salts: {}, {}", salt1, salt2);

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2);
    }

    @Test
    void testHashWithSalt() throws Exception {
        String input = "TestInput";
        String salt = UtilityFunctions.generateSalt();

        String hash = UtilityFunctions.hashWithSalt(input, salt);
        log.info("Generated hash: {}", hash);

        assertNotNull(hash);

        // Ensure that the same input and salt always generate the same hash
        String repeatedHash = UtilityFunctions.hashWithSalt(input, salt);
        assertEquals(hash, repeatedHash);

        // Ensure that different salts generate different hashes
        String differentSalt = UtilityFunctions.generateSalt();
        String differentHash = UtilityFunctions.hashWithSalt(input, differentSalt);
        assertNotEquals(hash, differentHash);
    }
}
