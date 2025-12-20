package me.gg.pinit;

import me.gg.pinit.infrastructure.jwt.JwtTokenProvider;
import me.gg.pinit.utils.TestKeys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@ActiveProfiles("test")
@SpringBootTest
class PinitAuthApplicationTests {

    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        JwtTokenProvider testJwtTokenProvider() {
            return new JwtTokenProvider(TestKeys.privateKey(), TestKeys.publicKey(), "https://pinit.go-gradually.me", Duration.ofMinutes(5), Duration.ofDays(14));
        }
    }
}
