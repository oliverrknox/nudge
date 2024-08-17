package net.gb.knox.nudge.fixture.annotation;

import net.gb.knox.nudge.fixture.JwtFixture;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {
    @Override
    public SecurityContext createSecurityContext(WithMockJwt annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        var principal = JwtFixture.JWT;
        var authorities = Arrays.stream(annotation.roles()).map(SimpleGrantedAuthority::new).toList();

        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
        return context;
    }
}
