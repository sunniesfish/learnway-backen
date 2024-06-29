package com.learnway.member.service;

import com.learnway.consult.domain.ConsultantRepository;
import com.learnway.member.domain.Member;
import com.learnway.member.domain.MemberRepository;
import com.learnway.member.domain.MemberRole;
import com.learnway.member.domain.MemberTelecom;
import com.learnway.member.dto.JoinDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 멤버 관련 서비스 클래스
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ConsultantRepository consultantRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;  // 비밀번호 암호화 저장

    // 회원 가입
    public void joinMember(JoinDTO joinDTO){

        // ID 중복 체크
        if (memberRepository.findByMemberId(joinDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }

        // 비밀번호 확인
        if (!joinDTO.getPassword().equals(joinDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Member member = Member.builder()
                .memberId(joinDTO.getUsername())
                .memberPw(bCryptPasswordEncoder.encode(joinDTO.getPassword())) // 비밀번호 암호화
                .memberName(joinDTO.getName())
                .memberBirth(LocalDate.parse(joinDTO.getBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) // 날짜 포맷 설정
                .memberPhone(joinDTO.getPhone())
                .memberTelecom(MemberTelecom.valueOf(joinDTO.getTelecom())) // ENUM
                .memberRole(MemberRole.ROLE_USER) // ENUM
                .memberSchool(joinDTO.getSchool())
                .memberGrade(joinDTO.getGrade())
                .memberAddress(joinDTO.getAddress())
                .memberDetailadd(joinDTO.getDetailAddress())
                .build();
        memberRepository.save(member);
        System.out.println("회원가입 완료!");
    }

    // ID 중복 체크
    public boolean isUsernameTaken(String username) {
        return memberRepository.findByMemberId(username).isPresent()
                || consultantRepository.findByConsultantId(username).isPresent();
    }
}
