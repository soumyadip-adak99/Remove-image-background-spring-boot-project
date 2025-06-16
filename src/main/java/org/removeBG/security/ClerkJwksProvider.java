package org.removeBG.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ClerkJwksProvider {

    @Value("${clerk.jwks-url}")
    private String jwksUrl;

    private final Map<String, PublicKey> keyCache = new HashMap<>();
    private long lastFetchTime = 0;
    private static final long CACHE_TIME_TO_LIVE = 3600000; // 1 hour in milliseconds

    public PublicKey getPublicKey(String keyId) throws Exception {
        // Fixed cache logic: refresh if key not found OR cache expired
        if (!keyCache.containsKey(keyId) || System.currentTimeMillis() - lastFetchTime > CACHE_TIME_TO_LIVE) {
            log.info("🔄 Refreshing JWKS keys from Clerk");
            refreshKeys();
        }

        PublicKey key = keyCache.get(keyId);
        if (key == null) {
            throw new Exception("Public key not found for keyId: " + keyId);
        }

        return key;
    }

    private void refreshKeys() throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL(jwksUrl));
            JsonNode keys = root.get("keys");

            if (keys == null || !keys.isArray()) {
                throw new Exception("Invalid JWKS response: no keys array found");
            }

            keyCache.clear(); // Clear old keys

            for (JsonNode key : keys) {
                // Fixed: JWT headers use 'kid' not 'keyID'
                String keyId = key.get("kid").asText();
                String keyType = key.get("kty").asText(); // 'kty' not 'keyType'
                String algorithm = key.get("alg").asText(); // 'alg' not 'algorithm'

                if ("RSA".equals(keyType) && "RS256".equals(algorithm)) {
                    String modulus = key.get("n").asText(); // 'n' not 'modulus'
                    String exponent = key.get("e").asText(); // 'e' not 'exponent'

                    PublicKey publicKey = createPublicKey(modulus, exponent);
                    keyCache.put(keyId, publicKey);
                    log.info("Cached public key for keyId: {}", keyId);
                }
            }

            lastFetchTime = System.currentTimeMillis();
            log.info("Successfully refreshed {} JWKS keys", keyCache.size());

        } catch (Exception e) {
            log.error("Failed to refresh JWKS keys", e);
            throw new Exception("Failed to fetch JWKS keys: " + e.getMessage());
        }
    }

    private PublicKey createPublicKey(String modulus, String exponent) throws Exception {
        try {
            byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
            byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);

            BigInteger modulusBigInteger = new BigInteger(1, modulusBytes);
            BigInteger exponentBigInteger = new BigInteger(1, exponentBytes);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusBigInteger, exponentBigInteger);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.error("Failed to create RSA public key", e);
            throw new Exception("Failed to create RSA public key: " + e.getMessage());
        }
    }
}