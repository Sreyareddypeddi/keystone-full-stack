package com.keystone.security;

import com.keystone.repository.AppUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

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
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth ->
                auth
                    // Allow browser CORS preflight requests
                    .requestMatchers(
                        org.springframework.http.HttpMethod.OPTIONS,
                        "/**"
                    )
                    .permitAll()

                    // Public authentication endpoints
                    .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                    )
                    .permitAll()

                    // Everything else requires authentication
                    .anyRequest()
                    .authenticated()
            )

            .addFilterBefore(
                filter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // =========================================================
    // CORS
    // =========================================================

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        /*
         * Accept:
         * 1. Your Vercel frontend
         * 2. Other Vercel preview URLs
         * 3. Local Vite development
         * 4. Local React development
         */
        config.setAllowedOriginPatterns(
            List.of(
                "https://*.vercel.app",
                "http://localhost:*",
                "http://127.0.0.1:*"
            )
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

        config.setExposedHeaders(
            List.of("Authorization")
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
                                    "ROLE_" + user.getRole()
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