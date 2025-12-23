package com.example.erp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration //설정 파일
@EnableJpaAuditing //JPA에게 데이터 변하는지 확인 요청
public class JpaAuditConfig {
}
