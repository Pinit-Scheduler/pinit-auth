package me.gg.pinit.authenticate.config;

import me.gg.pinit.adaptor.JwtTokenProvider;
import me.gg.pinit.adaptor.RsaKeyProvider;
import me.gg.pinit.authenticate.filter.JwtAuthenticationFilter;
import me.gg.pinit.authenticate.provider.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/refresh", "/login/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                jwtAuthenticationFilter(authenticationManager),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager);
    }

    @Bean
    public AuthenticationManager authenticationManager(JwtAuthenticationProvider jwtAuthenticationProvider) {
        return new ProviderManager(jwtAuthenticationProvider);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthenticationProvider(jwtTokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        PrivateKey privateKey = RsaKeyProvider.loadPrivateKey("keys/private_key.pem");
        PublicKey publicKey = RsaKeyProvider.loadPublicKey("keys/public_key.pem");
        return new JwtTokenProvider(privateKey, publicKey, "https://pinit.go-gradually.me", Duration.ofMinutes(5), Duration.ofDays(14));
    }
}
