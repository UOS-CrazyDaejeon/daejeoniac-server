package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

public class AppleResponse {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String id_token;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class ApplePublicKeys {
        private List<ApplePublicKey> keys;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class ApplePublicKey {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }

    public record AppleProfile(
            String subject,
            String email
    ) {
    }
}
