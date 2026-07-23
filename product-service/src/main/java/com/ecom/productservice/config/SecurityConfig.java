package com.ecom.productservice.config;

import com.ecom.productservice.security.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Anyone with a valid JWT can view products
                        .requestMatchers(HttpMethod.GET, "/product/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Internal/order-flow endpoint: called by order-service
                        // whenever a user places an order. Must be declared BEFORE
                        // the general PUT rule below, since Spring Security uses
                        // the first matching rule.
                        .requestMatchers(HttpMethod.PUT, "/product/reduce-stock/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Only ADMIN can update products
                        .requestMatchers(HttpMethod.PUT, "/product/**")
                        .hasRole("ADMIN")

                        // Only ADMIN can add products
                        .requestMatchers(HttpMethod.POST, "/product/**")
                        .hasRole("ADMIN")

                        // Only ADMIN can delete products
                        .requestMatchers(HttpMethod.DELETE, "/product/**")
                        .hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}