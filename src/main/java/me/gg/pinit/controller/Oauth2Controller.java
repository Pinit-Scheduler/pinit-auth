package me.gg.pinit.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import me.gg.pinit.controller.dto.LoginResponse;
import me.gg.pinit.controller.dto.SocialLoginResult;
import me.gg.pinit.domain.member.Member;
import me.gg.pinit.infra.JwtTokenProvider;
import me.gg.pinit.service.Oauth2Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class Oauth2Controller {
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Service oauth2Service;

    public Oauth2Controller(JwtTokenProvider jwtTokenProvider, Oauth2Service oauth2Service) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oauth2Service = oauth2Service;
    }

    // Todo 리다이렉트 준비 로직 추가

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
}