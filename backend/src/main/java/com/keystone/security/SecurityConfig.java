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

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtFilter filter
    ) throws Exception {

        http
            .csrf(c -> c.disable())

            // Enable CORS using the configuration below
            .cors(c -> c.configurationSource(cors()))

            // JWT based authentication - no server session
            .sessionManagement(s ->
                s.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(a ->
                a
                    // -------------------------------------------------
                    // OPTIONS / CORS PREFLIGHT
                    // -------------------------------------------------
                    .requestMatchers(
                        org.springframework.http.HttpMethod.OPTIONS,
                        "/**"
                    )
                    .permitAll()

                    // -------------------------------------------------
                    // PUBLIC ENDPOINTS
                    // -------------------------------------------------
                    .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                    )
                    .permitAll()

                    // -------------------------------------------------
                    // EVERYTHING ELSE REQUIRES LOGIN
                    // -------------------------------------------------
                    .anyRequest()
                    .authenticated()
            )

            // JWT filter runs before Spring's username/password filter
            .addFilterBefore(
                filter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    CorsConfigurationSource cors() {

        CorsConfiguration config =
                new CorsConfiguration();

        List<String> allowedOrigins =
                new ArrayList<>();

        // ---------------------------------------------------------
        // LOCAL FRONTEND
        // ---------------------------------------------------------

        allowedOrigins.add(
                "http://localhost:5173"
        );

        allowedOrigins.add(
                "http://localhost:3000"
        );

        // ---------------------------------------------------------
        // DEPLOYED VERCEL FRONTEND
        // ---------------------------------------------------------

        if (frontendUrl != null &&
                !frontendUrl.isBlank()) {

            allowedOrigins.add(
                    frontendUrl.trim()
            );
        }

        config.setAllowedOrigins(
                allowedOrigins
        );

        // ---------------------------------------------------------
        // ALLOWED HTTP METHODS
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // ALLOWED HEADERS
        // ---------------------------------------------------------

        config.setAllowedHeaders(
                List.of("*")
        );

        // ---------------------------------------------------------
        // CREDENTIALS
        // ---------------------------------------------------------

        config.setAllowCredentials(true);

        // ---------------------------------------------------------
        // REGISTER CORS FOR ALL ENDPOINTS
        // ---------------------------------------------------------

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }
}


// =============================================================
// JWT FILTER
// =============================================================

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

        // ---------------------------------------------------------
        // If Authorization header contains a Bearer token
        // ---------------------------------------------------------

        if (header != null &&
                header.startsWith("Bearer ")) {

            String token =
                    header.substring(7);

            // -----------------------------------------------------
            // Validate token
            // -----------------------------------------------------

            if (jwt.valid(token)) {

                String email =
                        jwt.extractEmail(token);

                // -------------------------------------------------
                // Find authenticated user
                // -------------------------------------------------

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

        // ---------------------------------------------------------
        // Continue request
        // ---------------------------------------------------------

        filterChain.doFilter(
                request,
                response
        );
    }
}