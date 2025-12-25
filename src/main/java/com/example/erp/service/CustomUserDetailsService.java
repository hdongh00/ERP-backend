package com.example.erp.service;

import com.example.erp.entity.Member;
import com.example.erp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //DB에서 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디가 없습니다: " + username));

        //스프링 시큐리티가 이해하는 User 객체로 변환해서 반환
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) //DB에 저장된 비밀번호
                .roles(member.getRole().name()) //USER 또는 ADMIN
                .build();
    }
}
