package com.learnway.member.service;

import com.learnway.consult.domain.ConsultantRepository;
import com.learnway.global.exceptions.S3Exception;
import com.learnway.global.service.S3ImageService; // 추가된 부분
import com.learnway.member.domain.*;
import com.learnway.member.dto.JoinDTO;
import com.learnway.member.dto.MemberUpdateDTO;
import com.learnway.member.dto.TargetUniDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

// 멤버 관련 서비스 클래스
@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ConsultantRepository consultantRepository;
    private final TargetUniRepository targetUniRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;  // 비밀번호 암호화 저장
    private final S3ImageService s3ImageService; // 추가된 부분

    //@Value("${upload.path}")/// application.properties 에 경로 명시한 부분 가져옴/
    private final String uploadPath = "";

    // ID 중복 체크 (컨설턴트까지 같이 비교)
    public boolean isUsernameTaken(String username) {
        return memberRepository.findByMemberId(username).isPresent()
                || consultantRepository.findByConsultantId(username).isPresent();
    }

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
            if (joinDTO.getImage() != null && !joinDTO.getImage().isEmpty()) {
                // 새로운 S3 업로드 로직 추가
                imagePath = s3ImageService.upload(joinDTO.getImage(), "images/member/");
            } else {
                // 이미지가 없을 경우 기본 이미지 경로 설정
                imagePath = "/img/member/member-default.png";
            }
        } catch (S3Exception e) {
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
                .memberGender(MemberGender.valueOf(joinDTO.getGender()))    // 성별 : ENUM
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

    // 수정폼 : 멤버 정보 불러오기
    public MemberUpdateDTO getMemberInfo(String username) {
        Member member = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        // 1지망, 2지망, 3지망 대학 불러오기
        List<TargetUniDTO> targetUnis = member.getTargetUnis().stream()
                .map(targetUni -> {
                    TargetUniDTO dto = new TargetUniDTO();
                    dto.setCollegeName(targetUni.getUniName());
                    dto.setRank(targetUni.getUniRank()); // rank : 해당 부분은 뷰에서 hidden
                    return dto;
                })
                .collect(Collectors.toList());

        return MemberUpdateDTO.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberBirth(member.getMemberBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .memberPhone(member.getMemberPhone())
                .memberTelecom(member.getMemberTelecom().name())
                .memberEmail(member.getMemberEmail())
                .memberGender(member.getMemberGender().name())
                .memberSchool(member.getMemberSchool())
                .memberGrade(member.getMemberGrade())
                .memberAddress(member.getMemberAddress())
                .memberDetailadd(member.getMemberDetailadd())
                .memberImage(member.getMemberImage()) // 기존 이미지 URL을 설정
                .targetUnis(targetUnis)
                .build();
    }

    // 멤버 정보 수정
    public void updateMemberInfo(String username, MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        // 비밀번호 경우 입력 시에만 일치 여부 확인
        if (memberUpdateDTO.getPassword() != null && !memberUpdateDTO.getPassword().isEmpty()) {
            if (!memberUpdateDTO.getPassword().equals(memberUpdateDTO.getConfirmPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            member = member.toBuilder()
                    .memberPw(bCryptPasswordEncoder.encode(memberUpdateDTO.getPassword()))
                    .build();
        }
        // 이미지 변경
        String imagePath = member.getMemberImage();
        if (memberUpdateDTO.getNewMemberImage() != null && !memberUpdateDTO.getNewMemberImage().isEmpty()) {
            try {
                String oldImagePath = imagePath; // 이전 이미지 경로 저장
                // 새로운 S3 업로드 로직 추가
                imagePath = s3ImageService.upload(memberUpdateDTO.getNewMemberImage(), "images/member/");
                // 새로운 S3 삭제 로직 추가
                s3ImageService.deleteImageFromS3(oldImagePath);
            } catch (S3Exception e) {
                throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
            }
        }
        member = member.toBuilder()
                .memberName(memberUpdateDTO.getMemberName())
                .memberBirth(LocalDate.parse(memberUpdateDTO.getMemberBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .memberPhone(memberUpdateDTO.getMemberPhone())
                .memberTelecom(MemberTelecom.valueOf(memberUpdateDTO.getMemberTelecom()))
                .memberEmail(memberUpdateDTO.getMemberEmail())
                .memberGender(MemberGender.valueOf(memberUpdateDTO.getMemberGender()))
                .memberSchool(memberUpdateDTO.getMemberSchool())
                .memberGrade(memberUpdateDTO.getMemberGrade())
                .memberAddress(memberUpdateDTO.getMemberAddress())
                .memberDetailadd(memberUpdateDTO.getMemberDetailadd())
                .memberImage(imagePath)
                .build();
        memberRepository.save(member);
        // 목표 대학 업데이트
        List<TargetUni> currentTargetUnis = member.getTargetUnis();
        // 목표 대학 컬럼 갯수 3개
        for (int i = 0; i < 3; i++) {
            TargetUniDTO targetUniDTO = memberUpdateDTO.getTargetUnis().get(i);
            // null 여부 확인 후 변경
            if (targetUniDTO.getCollegeName() != null && !targetUniDTO.getCollegeName().isEmpty()) {
                TargetUni targetUni = currentTargetUnis.stream()
                        .filter(t -> t.getUniRank().equals(targetUniDTO.getRank())) // 랭크 1,2,3 에 맞춰 변경
                        .findFirst()
                        .orElse(TargetUni.builder()
                                .uniRank(targetUniDTO.getRank())
                                .member(member)
                                .build());
                targetUni = targetUni.toBuilder()
                        .uniName(targetUniDTO.getCollegeName())
                        .build();
                targetUniRepository.save(targetUni);
            }
        }
        // 현재 세션의 사용자 정보를 업데이트
        CustomUserDetails updatedUserDetails = new CustomUserDetails(member);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
// 해당 부분은 S3 이용 안할 경우 사용되는 이미지 관련 메서드
    // // 이미지 생성 메서드
    // private String saveImage(MultipartFile image) throws IOException {
    //     if (image == null || image.isEmpty()) {
    //         return "/img/member/member-default.png"; // 기본 이미지 경로
    //     }
    //     // 중복 문제 해결 : 현재시간을 파일 이름에 추가
    //     String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
    //     Path imagePath = Paths.get(uploadPath, filename);
    //     Files.createDirectories(imagePath.getParent());
    //     Files.copy(image.getInputStream(), imagePath);

    //     return filename;
    // }

    // // 이미지 삭제 메서드
    // private void deleteImage(String imagePath) {
    //     if (imagePath != null && !imagePath.equals("/img/member/member-default.png")) {
    //         try {
    //             Path filePath = Paths.get(uploadPath).resolve(imagePath.replace("/uploads/", ""));
    //             Files.deleteIfExists(filePath);
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

    // 전체 멤버 조회 (어드민)
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    // 멤버 검색 (어드민)
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByMemberNameContainingIgnoreCase(name);
    }

    // 멤버 비고(노트) 업데이트 (어드민)
    public void updateMemberNote(Long id, String note) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member = member.toBuilder()
                .memberNote(note)
                .build();
        memberRepository.save(member);
    }
}