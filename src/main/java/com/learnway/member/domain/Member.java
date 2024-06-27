package com.learnway.member.domain;

import jakarta.persistence.*;
import lombok.*;

// Member 관련 엔티티 클래스
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 기본 키 값을 자동으로 관리 (MySQL -> AUTO INCREMENT)
    private Long id;               // PK
    @Column(unique = true)
    private String memberId;        // ID (Unique)
    private String memberPw;            // Password
    private String memberName;          // 성함
    /*private String memberBirth;         // 생년월일 (yyyy.MM.dd)
    private String memberEmail;         // E-mail
    private LocalDateTime memberCreate; // 가입일
    private String memberSchool;        // 학교
    @Check(constraints = "memberGrade >= 1 AND memberGrade <= 6")
    private int memberGrade;            // 학년 (Check 제약 조건 : 1~6 숫자)
    private String memberAddress;       // 주소
    private String memberDetailadd;     // 상세 주소 (주소 외 나머지 주소)
    private String memberPhone;         // 핸드폰 연락처
    private String memberImage;         // 프로필 이미지 / 기본 이미지 경로 추가 설정 필요 */
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;      // 회원 관리자 구분 (Enum : ROLE_ADMIN, ROLE_USER)
}