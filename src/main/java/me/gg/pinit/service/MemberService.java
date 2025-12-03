package me.gg.pinit.service;

import me.gg.pinit.domain.Member;
import me.gg.pinit.infra.JwtTokenProvider;
import me.gg.pinit.infra.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member login(String username, String password) {
        Member member = memberRepository.findAll().stream()
                .filter(m -> m.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return member;
    }

    public Member signup(String username, String password) {
        Member member = new Member(username, passwordEncoder.encode(password));
        return memberRepository.save(member);
    }
}
