package me.gg.pinit.infra.naver;

import me.gg.pinit.domain.oidc.OpenIdCommand;
import me.gg.pinit.domain.oidc.Profile;
import me.gg.pinit.infra.dto.OpenIdProfileResponse;
import me.gg.pinit.infra.dto.OpenIdTokenRequest;
import me.gg.pinit.infra.dto.OpenIdTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Objects;

public class NaverClient {
    private static final String OAUTH_2_0_TOKEN = "/oauth2.0/token";
    public static final String NAVER_ID_URL = "https://nid.naver.com";
    public static final String NAVER_APP_URL = "https://openapi.naver.com";
    private final RestClient loginClient;
    private final RestClient profileClient;
    private final NaverRegistrationProperties naverRegistrationProperties;

    public NaverClient(NaverRegistrationProperties naverRegistrationProperties) {
        loginClient = RestClient.builder()
                .baseUrl(NAVER_ID_URL).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        profileClient = RestClient.builder()
                .baseUrl(NAVER_APP_URL).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.naverRegistrationProperties = naverRegistrationProperties;
    }

    public OpenIdTokenResponse getToken(OpenIdCommand command) {
        return loginClient.post()
                .uri(OAUTH_2_0_TOKEN)
                .body(OpenIdTokenRequest.from(
                        command,
                        naverRegistrationProperties.getClientId(),
                        naverRegistrationProperties.getClientSecret(),
                        naverRegistrationProperties.getProvider()))
                .retrieve()
                .body(OpenIdTokenResponse.class);
    }

    public Profile getProfile(String accessToken) {
        OpenIdProfileResponse result = profileClient.get()
                .uri("/v1/nid/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(OpenIdProfileResponse.class);
        Objects.requireNonNull(result);
        return new Profile(result.getSub(), result.getNickname());
    }

}
