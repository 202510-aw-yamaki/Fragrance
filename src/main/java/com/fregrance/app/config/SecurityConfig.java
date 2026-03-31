package com.fregrance.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/css/**",
                    "/img/**",
                    "/api/**",
                    "/questionnaire",
                    "/questionnaire_step2",
                    "/fragrance-graph",
                    "/reservation",
                    "/reservation-complete",
                    "/staff/login")
                .permitAll()
                .requestMatchers("/staff/**")
                .hasRole("STAFF")
                .anyRequest()
                .permitAll())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .formLogin(form -> form
                .loginPage("/staff/login")
                .loginProcessingUrl("/staff/login")
                .defaultSuccessUrl("/staff/reservations", true)
                .failureUrl("/staff/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/staff/logout")
                .logoutSuccessUrl("/staff/login?logout"));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(
        @Value("${app.staff.username:rohera}") String username,
        @Value("${app.staff.password:Staff}") String password,
        PasswordEncoder passwordEncoder) {
        UserDetails staffUser = User.withUsername(username)
            .password(passwordEncoder.encode(password))
            .roles("STAFF")
            .build();
        return new InMemoryUserDetailsManager(staffUser);
    }
}