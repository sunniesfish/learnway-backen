package com.example.demo.dto;

import lombok.Data;

@Data
public class UserInfoDTO {

    private Long id;//임시 정보
    private String userName;//임시 정보
    private String userEmail;//임시 정보
    private String userAge;//임시 정보
    private String targetUniversity = "서울대학교";//임시 정보
    private String targetSubject = "법학과";//임시 정보
    private String stats = "필수 과목 평균 81점 상위 41%";//임시 정보
    private String sex = "남자";//임시 정보
    
    public UserInfoDTO() {
    }

	public UserInfoDTO(Long id, String userName, String userEmail, String userAge) {
		super();
		this.id = id;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userAge = userAge;
	}




}

