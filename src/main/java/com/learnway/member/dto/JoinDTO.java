package com.learnway.member.dto;

import lombok.Getter;
import lombok.Setter;

// 회원 가입 시 사용되는 DTO
@Getter
@Setter
public class JoinDTO {
    private String username;
    private String password;
    private String name;
}
