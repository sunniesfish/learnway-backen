package com.learnway.schedule.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.learnway.global.domain.Material;
import com.learnway.global.domain.Studyway;
import com.learnway.global.domain.Subject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Schedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//자동증가
	private Long scheduleId;
	
	@Column(nullable=true)
	private LocalDateTime  startTime;
	
	@Column(nullable=true)
	private LocalDateTime  endTime;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(columnDefinition = "VARCHAR(4)", unique = false)
	private Subject subjectId;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(columnDefinition = "VARCHAR(4)",unique = false)
	private Studyway studywayId;
	
	@OneToMany(mappedBy = "scheduleId", cascade = CascadeType.ALL)
	private List<Progress> progresses;


	
	
	
	
	
}