/*
package com.mayhotel.web_khachsan_nhom2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/account/**", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/account/login")
                        .loginProcessingUrl("/account/login")
                        // Đảm bảo khớp với thuộc tính 'name' trong <input> của file register.html
                        .usernameParameter("TenDangNhap")
                        .passwordParameter("MatKhau")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/account/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/account/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    // THAY ĐỔI Ở ĐÂY: Dùng NoOpPasswordEncoder thay vì BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}*/

