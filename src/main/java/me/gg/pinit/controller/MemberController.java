package me.gg.pinit.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import me.gg.pinit.controller.dto.LoginRequest;
import me.gg.pinit.controller.dto.LoginResponse;
import me.gg.pinit.controller.dto.SignupRequest;
import me.gg.pinit.domain.Member;
import me.gg.pinit.infra.JwtTokenProvider;
import me.gg.pinit.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Member member = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());

        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), Collections.emptyList());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                .body(new LoginResponse(accessToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        memberService.signup(signupRequest.getUsername(), signupRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return ResponseEntity.status(401).build();
        }

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        Long memberId = jwtTokenProvider.getMemberId(refreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(memberId, Collections.emptyList());

        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(newRefreshToken).toString())
                .body(new LoginResponse(newAccessToken));
    }

    @GetMapping("/me")
    public ResponseEntity<Void> checkLogin() {
        return ResponseEntity.ok().build();
    }

    private ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .build();
    }
}
