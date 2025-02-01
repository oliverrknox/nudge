package io.oliverknox.nudge.fixture;

import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

public abstract class JwtFixture {

    public static Jwt JWT = new Jwt("token", Instant.now(), Instant.now().plusSeconds(300),
            Map.of("Authorization", "Bearer token"),
            Map.of("sub", "d30d4015-9985-41d5-a201-b9575db5d239", "given_name", "Oliver", "email", "test@oliverknox.io"));
}
