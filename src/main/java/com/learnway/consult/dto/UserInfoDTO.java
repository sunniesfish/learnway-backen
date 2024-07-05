package com.learnway.consult.dto;

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
    private String userImg;
    private String requestContents;
    public UserInfoDTO() {
    }

	public UserInfoDTO(Long id, String userName , String userAge, String requestContents, String userImg) {
		super();
		this.id = id;
		this.userName = userName;
		this.userAge = userAge;
		this.requestContents = requestContents;
		this.userImg = userImg;

	}




}

