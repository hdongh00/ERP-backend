package com.example.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing //JPA Auditing 기능
public class AuditConfig {
    /**
     * 등록자/ 수정자를 처리해준느 빈 등록
     * DB에 저장/수정할 때마다 메서드 실행
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            //스프링 시큐리티에서 현재 인증 정보 가져옴
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //로그인이 안 되어 있거나, 인증되지 않은 사용자라면 null
            if(authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.empty();
            }
            //로그인한 사용자의 ID를 반환
            return Optional.of(authentication.getName());
        };
    }
}
