package com.meal_bridge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.meal_bridge.filter.JwtAuthenticationFilter;
import com.meal_bridge.models.entity.Role;
import com.meal_bridge.service.RoleBasedUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private RoleBasedUserDetailsService roleBasedUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        (req) -> req
                                .requestMatchers("/auth/client/register",
                                        "/auth/client/login",
                                        "/auth/mess-user/register",
                                        "/auth/mess-user/login",
                                        "/swagger-ui/index.html",
                                        "/v3/api-docs", "/swagger-ui.html",
                                        "/swagger-ui/swagger-ui.css",
                                        "/swagger-ui/index.css",
                                        "/swagger-ui/swagger-ui-bundle.js",
                                        "/swagger-ui/swagger-ui-standalone-preset.js",
                                        "/swagger-ui/swagger-initializer.js",
                                        "/swagger-ui/favicon-32x32.png",
                                        "/swagger-ui/favicon-16x16.png",
                                        "/v3/api-docs/swagger-config")
                                .permitAll()
                                .requestMatchers("/api/isTokenExpired")
                                .permitAll()
                                .requestMatchers("/api/user/**")
                                .hasAuthority(Role.USER.toString())
                                .requestMatchers("/api/mess/**")
                                .hasAnyAuthority(Role.ADMIN.toString(),
                                        Role.ASSISTANT.toString())
                                .anyRequest()
                                .denyAll())
                .userDetailsService(roleBasedUserDetailsService)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptions) -> exceptions
                        // Handles unauthorized access (no valid token)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter()
                                    .write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or missing token.\"}");
                        })
                        // Handles forbidden access (valid token but insufficient permissions)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Forbidden\", \"message\": \"You do not have permission to access this resource.\"}");
                        }))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }
}
