package com.learnway.schedule.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
	
	private Long scheduleId;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime  startTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime  endTime;
	private String studyway;
	private String subject;
	private String progress;
	private String material;
	private double achieveRate;
	
	@Override
	public String toString() {
		return "ScheduleDto [scheduleId=" + scheduleId + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", studyway=" + studyway + ", subject=" + subject + ", progress=" + progress + ", achieveRate="
				+ achieveRate + "]";
	}
	
	

}
