package com.learnway.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 학생 정보 중 목표 대학 정보를 담는 DTO
public class TargetUniDTO {
    private String collegeName;
    private String rank; // 지망 순위
}