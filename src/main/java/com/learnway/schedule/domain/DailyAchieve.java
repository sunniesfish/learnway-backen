package com.learnway.schedule.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_achieve",
		indexes = {@Index(name = "idx_member_date", columnList = "memberId, date")})
public class DailyAchieve {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//자동증가
	private Long DailyAchieveId;
	
	@Column
	private LocalDate date;
	
	@Column(nullable=true)
	private double avgAchieveRate;
	
	@Column
	private String memberId;
	
	@Override
	public String toString() {
		return "DailyAchieve [DailyAchieveId=" + DailyAchieveId + ", date=" + date + ", avgAchieveRate="
				+ avgAchieveRate + "]";
	}

}
