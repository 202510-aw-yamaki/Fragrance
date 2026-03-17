package com.fregrance.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/questionnaire",
                    "/questionnaire.html",
                    "/questionnaire/step2",
                    "/questionnaire_step2.html",
                    "/graph",
                    "/fragrance-graph.html",
                    "/reservation",
                    "/reservation.html",
                    "/reservation/complete",
                    "/reservation-complete.html",
                    "/css/**",
                    "/img/**",
                    "/api/health",
                    "/api/reservation-slots",
                    "/api/reservations/**",
                    "/api/questionnaire-results/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}