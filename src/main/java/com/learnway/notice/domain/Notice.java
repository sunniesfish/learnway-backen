package com.learnway.notice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//자동증가
	private Long noticeId;
	
	@Column
	private String noticeTitle;
	
	@Column
	private String noticeContent;
	
	@Column(nullable=true)
	private String noticeImgUname;
	
	@Column(nullable=true)
	private String noticeImgPath;
	
	@Column(nullable=true)
	private boolean topNotice;
	
}