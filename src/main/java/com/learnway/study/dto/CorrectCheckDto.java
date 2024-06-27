package com.learnway.study.dto;

import com.learnway.member.domain.Member;
import com.learnway.study.domain.Study;

import lombok.Data;

@Data
public class CorrectCheckDto {

	
	private Integer correctId;
	private String roomId;
	private Integer postId;
	
	private Member member;
	private Study study;
	private Integer status;
}
