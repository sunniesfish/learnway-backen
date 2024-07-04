package com.learnway.study.dto;

import java.util.List;

import lombok.Data;

@Data
public class StudyTagDto {

	private Integer postid; 
	private List tags;
	private String tag;
}
