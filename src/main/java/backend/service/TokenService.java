package backend.service;

import backend.User;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenService {

    private static final String SECRET = "change-this-secret-for-your-personal-project";
    private static final long TOKEN_TTL_SECONDS = 60L * 60L * 24L;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public String createToken(User user) {
        long expiresAt = Instant.now().getEpochSecond() + TOKEN_TTL_SECONDS;
        String payload = user.getId() + ":" + expiresAt;
        String encodedPayload = encode(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);

        return encodedPayload + "." + signature;
    }

    public Long verifyAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is required.");
        }

        return verifyToken(authorizationHeader.substring("Bearer ".length()));
    }

    private Long verifyToken(String token) {
        String[] parts = token.split("\\.");

        if (parts.length != 2 || !sign(parts[0]).equals(parts[1])) {
            throw new IllegalArgumentException("Invalid authorization token.");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String[] payloadParts = payload.split(":");

        if (payloadParts.length != 2) {
            throw new IllegalArgumentException("Invalid authorization token.");
        }

        long expiresAt = Long.parseLong(payloadParts[1]);

        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new IllegalArgumentException("Authorization token has expired.");
        }

        return Long.parseLong(payloadParts[0]);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(key);
            return encode(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign authorization token.", e);
        }
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
