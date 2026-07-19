package com.ecom.apigateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JWTAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    public JWTAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        // Public Endpoints
        if (path.startsWith("/auth") || path.startsWith("/eureka")) {
            return chain.filter(exchange);
        }

        // Read Authorization Header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Header Missing
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract Token
        String token = authHeader.substring(7);

        // Validate Token
        if (!jwtService.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract Username
        String username = jwtService.extractUserName(token);

        // TODO: Later we'll extract role from JWT instead of hardcoding ROLE_USER
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContextImpl securityContext =
                new SecurityContextImpl(authentication);

        return chain.filter(exchange)
                .contextWrite(
                        ReactiveSecurityContextHolder.withSecurityContext(
                                Mono.just(securityContext)
                        )
                );
    }
}