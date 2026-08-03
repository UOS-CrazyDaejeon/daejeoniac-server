package com.daejeongwang.uoscrazydaejeon.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

public class KakaoResponse {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class OAuthToken {

        private String access_token;

        private String token_type;

        private String refresh_token;

        private int expires_in;

        private String scope;

        private int refresh_token_expires_in;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class KakaoProfile {
        private Long id;
        private KakaoAccount kakao_account;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Profile {
        private String nickname;
    }
}
