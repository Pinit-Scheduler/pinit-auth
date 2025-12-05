package me.gg.pinit.domain.oidc.naver;

import lombok.Getter;

public class NaverOauth2Token {
    @Getter
    private String accessToken;
    @Getter
    private String refreshToken;
    @Getter
    private Long expiresIn;
    @Getter
    private String tokenType;

    public NaverOauth2Token(String accessToken, String refreshToken, Long expiresIn, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

}
