package com.learnway.schedule.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.learnway.global.domain.Material;
import com.learnway.global.domain.Studyway;
import com.learnway.global.domain.Subject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//자동증가
	private Long progressId;
	
	@Column(nullable=true)
	private double achieveRate;
	
	@Column(nullable=true)
	private String progress;
	
	@ManyToOne
	@JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId", insertable = true, updatable = true)
	private Schedule scheduleId;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "materialId",unique = false)
	private Material materialId;
}
