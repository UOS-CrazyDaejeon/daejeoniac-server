package com.daejeongwang.uoscrazydaejeon.util;

import com.daejeongwang.uoscrazydaejeon.dto.response.AppleResponse;
import com.daejeongwang.uoscrazydaejeon.exception.AppleApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class AppleUtil {

    private static final String APPLE_AUTH_URL = "https://appleid.apple.com";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    @Value("${apple.client-id:}")
    private String appleClientId;

    @Value("${apple.team-id:}")
    private String appleTeamId;

    @Value("${apple.key-id:}")
    private String appleKeyId;

    @Value("${apple.private-key:}")
    private String applePrivateKey;

    @Value("${apple.private-key-path:}")
    private String applePrivateKeyPath;

    @Value("${apple.redirect-url:}")
    private String appleRedirectUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AppleResponse.OAuthToken requestAppleToken(String appleAuthorizationCode) {
        validateAppleProperties();

        try {
            String responseBody = WebClient.create(APPLE_AUTH_URL)
                    .post()
                    .uri("/auth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", appleClientId)
                            .with("client_secret", createAppleClientSecret())
                            .with("code", appleAuthorizationCode)
                            .with("redirect_uri", appleRedirectUrl))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(responseBody, AppleResponse.OAuthToken.class);

        } catch (WebClientResponseException e) {
            throw new AppleApiException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "애플 토큰 요청 실패: " + e.getResponseBodyAsString()
            );
        } catch (JsonProcessingException e) {
            throw new AppleApiException(HttpStatus.BAD_GATEWAY, "애플 토큰 응답 파싱 실패");
        }
    }

    public AppleResponse.AppleProfile parseAppleProfile(String appleIdToken) {
        try {
            String keyId = getKeyId(appleIdToken);
            PublicKey publicKey = getApplePublicKey(keyId);

            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(APPLE_ISSUER)
                    .requireAudience(appleClientId)
                    .build()
                    .parseSignedClaims(appleIdToken)
                    .getPayload();

            return new AppleResponse.AppleProfile(
                    claims.getSubject(),
                    claims.get("email", String.class)
            );

        } catch (AppleApiException e) {
            throw e;
        } catch (Exception e) {
            throw new AppleApiException(HttpStatus.UNAUTHORIZED, "애플 ID 토큰 검증 실패");
        }
    }

    private String createAppleClientSecret() {
        try {
            Date now = Date.from(Instant.now());
            Date expiration = Date.from(Instant.now().plusSeconds(60L * 60L * 24L * 30L));

            return Jwts.builder()
                    .header()
                    .keyId(appleKeyId)
                    .and()
                    .issuer(appleTeamId)
                    .issuedAt(now)
                    .expiration(expiration)
                    .audience()
                    .add(APPLE_ISSUER)
                    .and()
                    .subject(appleClientId)
                    .signWith(getApplePrivateKey(), Jwts.SIG.ES256)
                    .compact();

        } catch (Exception e) {
            throw new AppleApiException(HttpStatus.INTERNAL_SERVER_ERROR, "애플 client_secret 생성 실패");
        }
    }

    private PrivateKey getApplePrivateKey() throws Exception {
        String privateKey = getApplePrivateKeyText()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

        return KeyFactory.getInstance("EC").generatePrivate(keySpec);
    }

    private String getApplePrivateKeyText() throws Exception {
        if (StringUtils.hasText(applePrivateKey)) {
            return applePrivateKey.replace("\\n", "\n");
        }

        if (StringUtils.hasText(applePrivateKeyPath)) {
            return Files.readString(Path.of(applePrivateKeyPath), StandardCharsets.UTF_8);
        }

        throw new IllegalStateException("apple.private-key 또는 apple.private-key-path 설정이 필요합니다.");
    }

    private PublicKey getApplePublicKey(String keyId) {
        try {
            String responseBody = WebClient.create(APPLE_AUTH_URL)
                    .get()
                    .uri("/auth/keys")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            AppleResponse.ApplePublicKeys publicKeys =
                    objectMapper.readValue(responseBody, AppleResponse.ApplePublicKeys.class);

            AppleResponse.ApplePublicKey applePublicKey = publicKeys.getKeys()
                    .stream()
                    .filter(key -> keyId.equals(key.getKid()))
                    .findFirst()
                    .orElseThrow(() -> new AppleApiException(HttpStatus.UNAUTHORIZED, "애플 공개키를 찾을 수 없습니다."));

            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(applePublicKey.getN()));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(applePublicKey.getE()));
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);

            return KeyFactory.getInstance("RSA").generatePublic(keySpec);

        } catch (AppleApiException e) {
            throw e;
        } catch (Exception e) {
            throw new AppleApiException(HttpStatus.BAD_GATEWAY, "애플 공개키 요청 실패");
        }
    }

    private String getKeyId(String appleIdToken) throws JsonProcessingException {
        String encodedHeader = appleIdToken.split("\\.")[0];
        String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader), StandardCharsets.UTF_8);

        JsonNode header = objectMapper.readTree(decodedHeader);

        return header.get("kid").asText();
    }

    private void validateAppleProperties() {
        if (!StringUtils.hasText(appleClientId)
                || !StringUtils.hasText(appleTeamId)
                || !StringUtils.hasText(appleKeyId)
                || !StringUtils.hasText(appleRedirectUrl)) {
            throw new AppleApiException(HttpStatus.INTERNAL_SERVER_ERROR, "애플 로그인 설정이 필요합니다.");
        }
    }
}
