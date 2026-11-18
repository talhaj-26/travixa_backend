package com.travixa.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // This enables @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthFilter,
                                           CustomUserDetailsService userDetailsService,
                                           AuthEntryPointJwt unauthorizedHandler,
                                           SimpleAccessDeniedHandler accessDeniedHandler,
                                           CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                // The API authenticates with bearer JWTs and does not use browser-managed
                // session cookies, so CSRF protection is not applicable to these requests.
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(org.springframework.http.HttpMethod.POST,
                                "/api/auth/signin", "/api/auth/signup", "/api/auth/login",
                                "/api/auth/register", "/api/auth/refresh",
                                "/api/auth/forgot-password", "/api/auth/validate-reset-token",
                                "/api/auth/reset-password")
                        .permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/media/**").permitAll()
                        .requestMatchers("/api/admin/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/super-admin/**")
                        .hasAuthority("SUPER_ADMIN")
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/destinations",
                                "/api/destinations/**"
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/transport-schedules/public").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/guide-offerings/public").permitAll()
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/tours-packages",
                                "/api/tours-packages/**"
                        ).permitAll()
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/hotels",
                                "/api/hotels/**"
                        ).permitAll()
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/restaurants",
                                "/api/restaurants/**",
                                "/api/menu-items",
                                "/api/menu-items/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler) // Handle authentication errors
                        .accessDeniedHandler(accessDeniedHandler) // Handle authorization errors
                )
                .authenticationProvider(authenticationProvider(userDetailsService))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
