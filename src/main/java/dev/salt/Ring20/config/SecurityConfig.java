package dev.salt.Ring20.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value(
            "${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:4173,https://frontend-training.up.railway.app,https://ringsatranarvi.se,https://www.ringsatranarvi.se,https://*.ngrok-free.app,https://staging-ringsatranarvi-app.web.app,https://prod-ringsatranarvi-app.web.app}")
    private String allowedOrigins;

    private static final String LOCAL_HOST_1573 = "http://localhost:5173";
    private static final String LOCAL_HOST_8081 = "http://localhost:8081";
    private static final String CLERK_JWT_URI =
            "https://unique-man-24.clerk.accounts.dev/.well-known/jwks.json";
    private static final List<String> METHOD_LIST =
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/users/*",
                                                "/api/users/*/progress")
                                        .permitAll()
                                        .requestMatchers("/api/users/me/**")
                                        .authenticated()
                                        .requestMatchers(HttpMethod.POST, "/api/activity-logs")
                                        .authenticated()
                                        .anyRequest()
                                        .permitAll()) // Dev mode: endpoints are protected
                // individually
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins =
                new ArrayList<>(
                        Arrays.stream(allowedOrigins.split(","))
                                .map(String::trim)
                                .filter(origin -> !origin.isEmpty())
                                .toList());

        if (!origins.contains(LOCAL_HOST_1573)) {
            origins.add(LOCAL_HOST_1573);
        }
        if (!origins.contains(LOCAL_HOST_8081)) {
            origins.add(LOCAL_HOST_8081);
        }

        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(METHOD_LIST);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(CLERK_JWT_URI).build();
    }
}
