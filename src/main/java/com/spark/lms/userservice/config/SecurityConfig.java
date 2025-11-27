package com.spark.lms.userservice.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // pattern for your APIs
    private static final String API_URL_PATTERN = "/api/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   HandlerMappingIntrospector introspector,
                                                   JwtAuthFilter jwtAuthFilter) throws Exception {

        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(
                        mvc.pattern(API_URL_PATTERN),
                        PathRequest.toH2Console()
                )
        );

        http.headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
        );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        mvc.pattern("/api/users/register"),
                        mvc.pattern("/api/users/login")
                ).permitAll()

                .requestMatchers(PathRequest.toH2Console()).permitAll()

                .anyRequest().authenticated()
        );

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.httpBasic(httpBasic -> httpBasic.disable());
        http.formLogin(form -> form.disable());

        // 🔥 VERY IMPORTANT — trust API Gateway authentication
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
