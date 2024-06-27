package com.learnway.schedule.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DailyAchieve {
	
	@Id
	private String DailyAchieveId;
	
	@Column(nullable=true)
	private LocalDateTime date;
	
	@Column(nullable=true)
	private double avgAchieveRate;
	
	@Override
	public String toString() {
		return "DailyAchieve [DailyAchieveId=" + DailyAchieveId + ", date=" + date + ", avgAchieveRate="
				+ avgAchieveRate + "]";
	}

}
