package com.bekrenovr.ecommerce.apigateway.auth;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class OptionalAuthAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private Collection<GrantedAuthority> requiredAuthoritiesIfAuthenticated;

    public OptionalAuthAuthorizationManager(String... requiredAuthoritiesIfAuthenticated) {
        this.requiredAuthoritiesIfAuthenticated = Arrays.stream(requiredAuthoritiesIfAuthenticated)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .map(auth -> {
                    boolean granted = requiredAuthoritiesIfAuthenticated.isEmpty() || hasAnyRequiredAuthority(auth);
                    return new AuthorizationDecision(granted);
                })
                .defaultIfEmpty(new AuthorizationDecision(true));
    }

    private boolean hasAnyRequiredAuthority(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> requiredAuthoritiesIfAuthenticated.contains(authority));
    }
}
