package com.learnway.member.service;

import com.learnway.member.domain.Member;
import com.learnway.member.domain.MemberRepository;
import com.learnway.member.domain.MemberRole;
import com.learnway.member.dto.JoinDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 멤버 관련 서비스 클래스
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;  // 비밀번호 암호화 저장

    // 회원 가입
    public void joinMember(JoinDTO joinDTO){

        // ID 중복 체크
        if (memberRepository.findByMemberId(joinDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }

        Member member = Member.builder()
                .memberId(joinDTO.getUsername())
                .memberPw(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                .memberName(joinDTO.getName())
                .memberRole(MemberRole.ROLE_USER)   // 신규 가입 시 Role -> USER
                .build();
        memberRepository.save(member);
        System.out.println("회원가입 완료!");
    }

}
