package com.fregrance.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${app.staff.username:rohera}")
    private String staffUsername;

    @Value("${app.staff.password:Staff}")
    private String staffPassword;

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
                    "/staff/login",
                    "/css/**",
                    "/img/**",
                    "/api/health",
                    "/api/reservation-slots",
                    "/api/reservations/**",
                    "/api/questionnaire-results/**"
                ).permitAll()
                .requestMatchers("/staff/**").hasRole("STAFF")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/staff/login")
                .defaultSuccessUrl("/staff/reservations", true)
                .permitAll()
            )
            .logout(logout -> logout.logoutSuccessUrl("/staff/login?logout"))
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername(staffUsername)
                .password(passwordEncoder.encode(staffPassword))
                .roles("STAFF")
                .build()
        );
    }
}
