package com.keystone.security;

import com.keystone.repository.AppUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.stereotype.Component;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtFilter filter
    ) throws Exception {

        http
            .csrf(c -> c.disable())

            .cors(c -> c.configurationSource(cors()))

            .sessionManagement(s ->
                s.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(a ->
                a.requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                filter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    CorsConfigurationSource cors() {

        CorsConfiguration config =
                new CorsConfiguration();

        List<String> allowedOrigins =
                new ArrayList<>();

        // Local development
        allowedOrigins.add(
                "http://localhost:5173"
        );

        allowedOrigins.add(
                "http://localhost:3000"
        );

        // Production Vercel frontend
        if (frontendUrl != null &&
                !frontendUrl.isBlank()) {

            allowedOrigins.add(
                    frontendUrl.trim()
            );
        }

        config.setAllowedOrigins(
                allowedOrigins
        );

        config.setAllowedMethods(
                List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "PATCH",
                    "OPTIONS"
                )
        );

        config.setAllowedHeaders(
                List.of("*")
        );

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }
}


@Component
class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final AppUserRepository users;

    JwtFilter(
            JwtService jwt,
            AppUserRepository users
    ) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header =
                request.getHeader("Authorization");

        if (header != null &&
                header.startsWith("Bearer ")) {

            String token =
                    header.substring(7);

            if (jwt.valid(token)) {

                String email =
                        jwt.extractEmail(token);

                users.findByEmail(email)
                        .ifPresent(user -> {

                    var authority =
                            new org.springframework
                                .security
                                .core
                                .authority
                                .SimpleGrantedAuthority(
                                    "ROLE_" +
                                    user.getRole()
                                );

                    var authentication =
                            new org.springframework
                                .security
                                .authentication
                                .UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    null,
                                    List.of(authority)
                                );

                    org.springframework
                        .security
                        .core
                        .context
                        .SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                            authentication
                        );
                });
            }
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}