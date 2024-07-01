package com.learnway.study.domain;

import com.learnway.member.domain.Member;

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

@Getter
@Table(name="CorrectCheck")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class CorrectCheck {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="CorrectCk_id" , nullable = false)
	private Integer correctId;
	
	@ManyToOne
	@JoinColumn(name = "id", nullable = false)
    private Member member;
	
	@ManyToOne
	@JoinColumn(name = "study_postid", nullable = false)
    private Study study;
	
	@Column(name="answer_status" , nullable = false)
	private Integer status;
}
