package me.gg.pinit.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import me.gg.pinit.controller.dto.LoginResponse;
import me.gg.pinit.controller.dto.OauthLoginSetting;
import me.gg.pinit.controller.dto.SocialLoginResult;
import me.gg.pinit.domain.member.Member;
import me.gg.pinit.domain.oidc.Oauth2Provider;
import me.gg.pinit.infra.JwtTokenProvider;
import me.gg.pinit.service.Oauth2ProviderMapper;
import me.gg.pinit.service.Oauth2Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@RestController
public class Oauth2Controller {
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Service oauth2Service;
    private final Oauth2ProviderMapper oauth2ProviderMapper;

    public Oauth2Controller(JwtTokenProvider jwtTokenProvider, Oauth2Service oauth2Service, Oauth2ProviderMapper oauth2ProviderMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oauth2Service = oauth2Service;
        this.oauth2ProviderMapper = oauth2ProviderMapper;
    }

    @GetMapping("/login/oauth2/authorize/{provider}")
    public ResponseEntity<Void> authorize(@PathVariable String provider, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String state = oauth2Service.generateState(sessionId);

        OauthLoginSetting loginSetting = buildOauthLoginSetting(state, provider, request);
        String authorizationUri = UriComponentsBuilder.fromUri(oauth2Service.getAuthorizationUri(provider, state))
                .queryParam("response_type", loginSetting.getResponse_type())
                .queryParam("client_id", loginSetting.getClient_id())
                .queryParam("redirect_uri", loginSetting.getRedirect_uri())
                .queryParam("state", loginSetting.getState())
                .build()
                .toUriString();


        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, authorizationUri)
                .build();
    }


    @GetMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<LoginResponse> socialLogin(@PathVariable String provider, @ModelAttribute SocialLoginResult socialLoginResult, HttpServletRequest request) {
        if (socialLoginResult.getError() != null) {
            return ResponseEntity.badRequest().build();
        }

        HttpSession session = request.getSession(false);
        String sessionId = session != null ? session.getId() : null;

        Member member = oauth2Service.login(provider, sessionId, socialLoginResult.getCode(), socialLoginResult.getState());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), Collections.emptyList());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                .body(new LoginResponse(accessToken));
    }

    private ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .build();
    }

    private OauthLoginSetting buildOauthLoginSetting(String state, String provider, HttpServletRequest request) {
        OauthLoginSetting loginSetting = new OauthLoginSetting();
        Oauth2Provider oauth2Provider = oauth2ProviderMapper.get(provider);
        loginSetting.setResponse_type("code");
        loginSetting.setClient_id(oauth2Provider.getClientId());
        loginSetting.setRedirect_uri(resolveRedirectUri(oauth2Provider, provider, request));
        loginSetting.setState(state);
        return loginSetting;
    }

    private String resolveRedirectUri(Oauth2Provider provider, String providerString, HttpServletRequest request) {
        String redirectUri = provider.getRedirectUri();
        return redirectUri
                .replace("{baseUrl}", getBaseUrl(request))
                .replace("{registrationId}", providerString);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        String requestUri = request.getRequestURI();
        return requestUrl.substring(0, requestUrl.length() - requestUri.length());
    }
}
