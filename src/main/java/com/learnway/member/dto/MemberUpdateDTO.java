package com.learnway.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDTO {
    @NotEmpty(message = "아이디는 필수 입력 항목입니다.")
    private String memberId;

    @NotEmpty(message = "이름은 필수 입력 항목입니다.")
    private String memberName;

    private String password;
    private String confirmPassword;

    @NotEmpty(message = "생년월일은 필수 입력 항목입니다.")
    private String memberBirth;

    @NotEmpty(message = "연락처는 필수 입력 항목입니다.")
    private String memberPhone;

    @NotEmpty(message = "통신사는 필수 입력 항목입니다.")
    private String memberTelecom;

    @NotEmpty(message = "이메일은 필수 입력 항목입니다.")
    private String memberEmail;

    private String memberSchool;
    private int memberGrade;
    private String memberAddress;
    private String memberDetailadd;
    private String memberImage; // 이미지 URL
    private MultipartFile newMemberImage; // 새로운 이미지 파일

    private List<TargetUniDTO> targetUnis;
}