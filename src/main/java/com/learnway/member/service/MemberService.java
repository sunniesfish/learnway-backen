package com.learnway.member.service;

import com.learnway.consult.domain.ConsultantRepository;
import com.learnway.member.domain.*;
import com.learnway.member.dto.JoinDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 멤버 관련 서비스 클래스
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ConsultantRepository consultantRepository;
    private final TargetUniRepository targetUniRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;  // 비밀번호 암호화 저장

    @Value("${upload.path}") // application.properties 에 경로 명시한 부분 가져옴
    private String uploadPath;

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

        // 이미지 저장 후 경로 저장
        String imagePath;
        try {
            imagePath = saveImage(joinDTO.getImage());
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
        }

        Member member = Member.builder()
                .memberId(joinDTO.getUsername())             // ID
                .memberPw(bCryptPasswordEncoder.encode(joinDTO.getPassword())) // 비밀번호 : 암호화
                .memberName(joinDTO.getName())               // 이름
                .memberBirth(LocalDate.parse(joinDTO.getBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) // 생년월일 : 날짜 포맷 설정
                .memberPhone(joinDTO.getPhone())             // 연락처
                .memberTelecom(MemberTelecom.valueOf(joinDTO.getTelecom())) // 통신사 : ENUM
                .memberRole(MemberRole.ROLE_USER)                           // 멤버 권한 : ENUM
                .memberEmail(joinDTO.getEmail())             // 이메일
                .memberSchool(joinDTO.getSchool())           // 학교
                .memberGrade(joinDTO.getGrade())             // 학년
                .memberAddress(joinDTO.getAddress())         // 주소
                .memberDetailadd(joinDTO.getDetailAddress()) // 나머지 주소
                .memberImage(imagePath)                      // 이미지 경로 저장
                .build();
        memberRepository.save(member);

        if (joinDTO.getTargetUni() != null) {
            joinDTO.getTargetUni().forEach(targetUniDTO -> {
                if (targetUniDTO.getCollegeName() != null && !targetUniDTO.getCollegeName().isEmpty()) {
                    TargetUni targetUni = TargetUni.builder()
                            .uniName(targetUniDTO.getCollegeName())
                            .uniRank(targetUniDTO.getRank())
                            .member(member)
                            .build();
                    targetUniRepository.save(targetUni);
                }
            });
        }
        System.out.println("회원가입 완료!");
    }

    // ID 중복 체크 (컨설턴트까지 같이 비교)
    public boolean isUsernameTaken(String username) {
        return memberRepository.findByMemberId(username).isPresent()
                || consultantRepository.findByConsultantId(username).isPresent();
    }

    // 이미지 저장 메서드
    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return "/img/member/member-default.png"; // 기본 이미지 경로
        }
        // 중복 문제 해결 : 현재시간을 파일 이름에 추가
        String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path imagePath = Paths.get(uploadPath, filename);
        Files.createDirectories(imagePath.getParent());
        Files.copy(image.getInputStream(), imagePath);

        return "/img/member/uploads/" + filename;
    }
}
