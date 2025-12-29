package com.example.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //비밀번호 암호화 기계(BCrypt) 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //보안 필터 체인
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                //누구나 접근 가능
                .requestMatchers("/login", "/css/**", "/js/**", "/error").permitAll()
                //관리자만 접근 가능
                .requestMatchers("api/approval/**", "/approval/**").hasRole("ADMIN")
                //나머지는 다 로그인해야 가능
                .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login") //로그인 폼이 전송될 주소
                        .defaultSuccessUrl("/", false) //로그인 성공 시 이동할 곳
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll())
                .csrf(csrf -> csrf.disable()); //개발 할 때를 위해 잠시 csrf 끔
        return http.build();
    }
}
