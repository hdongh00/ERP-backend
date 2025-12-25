package com.example.erp;

import com.example.erp.entity.Member;
import com.example.erp.entity.Role;
import com.example.erp.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ErpApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpApplication.class, args);
	}

    @Bean
    public CommandLineRunner initData(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 관리자 계정이 없으면 생성 (ID: admin, PW: 1234)
            if (memberRepository.findByUsername("admin").isEmpty()) {
                memberRepository.save(new Member("admin", passwordEncoder.encode("1234"), "관리자", Role.ADMIN));
            }
            // 사원 계정이 없으면 생성 (ID: user, PW: 1234)
            if (memberRepository.findByUsername("user").isEmpty()) {
                memberRepository.save(new Member("user", passwordEncoder.encode("1234"), "김사원", Role.USER));
            }
        };
    }
}
