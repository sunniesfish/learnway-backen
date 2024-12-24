package com.learnway.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {
	
	private Long progressId;
	private double achieveRate;
	private String materialId;
	private String progress;
	private String memberId;

    public ProgressDto(Long progressId, String materialId, double achieveRate, String progress) {
		this.progressId = progressId;
		this.materialId = materialId;
		this.achieveRate = achieveRate;
		this.progress = progress;
    }

    @Override
	public String toString() {
		return "ProgressDto [progressId=" + progressId + ", achieveRate=" + achieveRate + ", materialId=" + materialId
				+ ", progress=" + progress + "]";
	}


}
