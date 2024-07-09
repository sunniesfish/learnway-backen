package com.learnway.study.domain;

import com.learnway.member.domain.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Problems")
public class StudyProblem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="study_problemid" , nullable = false)
	private Integer problemid;
	
	@Column(name="study_subject" , nullable = false)
	private String subject;
	
	@Column(name="study_level" , nullable = false)
	private String level;
	
	@Column(name="study_content")
	private String content;
	
	@Column(name="study_correct")
	private String correct;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "study_postid", nullable = false)
	private Study study;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id", nullable = false)
	private Member memid;
	
}
