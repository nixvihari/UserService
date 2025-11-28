package com.spark.lms.userservice.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // 🔥 Enables @PreAuthorize and method-level RBAC
public class SecurityConfig {

    private static final String API_URL_PATTERN = "/api/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   HandlerMappingIntrospector introspector,
                                                   JwtAuthFilter jwtAuthFilter) throws Exception {

        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        // -----------------------------
        // CSRF Disabled for APIs
        // -----------------------------
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(
                        mvc.pattern(API_URL_PATTERN),
                        PathRequest.toH2Console()
                )
        );

        // Allow H2 console
        http.headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
        );

        // -----------------------------
        // ROLE & ACCESS RULES
        // -----------------------------
        http.authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers(
                        mvc.pattern("/api/users/register"),
                        mvc.pattern("/api/users/login")
                ).permitAll()

                .requestMatchers(PathRequest.toH2Console()).permitAll()

                // Admin-only endpoints
                .requestMatchers(mvc.pattern("/api/users/admin/**"))
                        .hasRole("ADMIN")

                .requestMatchers(mvc.pattern("/api/users/all"))
                        .hasRole("ADMIN")

                .requestMatchers(mvc.pattern("/api/users/search/**"))
                        .hasRole("ADMIN")

                .requestMatchers(mvc.pattern("/api/users/delete/**"))
                        .hasRole("ADMIN")

                // Everything else requires authentication
                .anyRequest().authenticated()
        );

        // -----------------------------
        // Stateless JWT Sessions
        // -----------------------------
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        
     // 🔥 Forward 401/403 exceptions to GlobalExceptionHandler
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    throw new org.springframework.security.authentication.BadCredentialsException("UNAUTHORIZED");
                })
                .accessDeniedHandler((req, res, e) -> {
                    throw new org.springframework.security.access.AccessDeniedException("FORBIDDEN");
                })
        );

        // Disable form login & basic auth
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.formLogin(form -> form.disable());

        // 🔥 IMPORTANT: Trust API Gateway (header-based authentication)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
