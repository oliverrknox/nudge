package io.oliverknox.nudge.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private Collection<GrantedAuthority> getAuthorities(Jwt jwt) {
        var roles = new HashSet<String>();

        Map<String, Collection<String>> realmAccessClaim = jwt.getClaim("realm_access");
        Map<String, Map<String, Collection<String>>> resourceAccessClaim = jwt.getClaim("resource_access");
        Collection<String> groupsClaim = jwt.getClaim("groups");

        if (realmAccessClaim != null) {
            var rolesClaim = realmAccessClaim.get("roles");
            if (rolesClaim != null) {
                roles.addAll(rolesClaim);
            }
        }

        if (resourceAccessClaim != null) {
            var clientClaim = resourceAccessClaim.get("client");
            if (clientClaim != null) {
                var rolesClaim = clientClaim.get("roles");
                if (rolesClaim != null) {
                    roles.addAll(rolesClaim);
                }
            }
        }

        if (groupsClaim != null) {
            roles.addAll(groupsClaim);
        }

        return roles.stream().map(role -> new SimpleGrantedAuthority(String.format("ROLE_%s", role))).collect(Collectors.toSet());
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        var authorities = getAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
