package com.learnway.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
	
	private Long noticeId;
	private String noticeTitle;
	private String noticeContent;
	private String noticeImgUname;
	private String noticeImgPath;
	private boolean topNotice;

}
