package com.example.erp.repository;

import com.example.erp.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //로그인 ID로 회원 찾기
    Optional<Member> findByUsername(String username);
}
