package com.restapiexample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (requests) -> requests
                        // Used for Selective Authentication
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/extended/**").authenticated()
                        // Used for Selective Role based Authentication
//                        .requestMatchers("/api/**").hasRole("USER")
//                        .requestMatchers("/extended/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
        ).httpBasic(withDefaults());
        http.csrf().disable();
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("COMMON_PASSWORD"))
                .roles("USER")
                .build();

        // Used for Selective Role based Authentication
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("COMMON_ADMIN_PASSWORD"))
//                .roles("ADMIN")
//                .build();

        return new InMemoryUserDetailsManager(user);
        // Used for Selective Role based Authentication
//        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
