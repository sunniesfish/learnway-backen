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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="study_chatroom")
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="study_chatroomid",nullable = false)
	private Integer chatroomid;
	
	@ManyToOne
	@JoinColumn(name = "study_postid", nullable = false)
	private Study study;
//	엔티티생성후 제거예정
	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
    private Member member;
	
	
	@Column(name="study_roomname",nullable = false)
	private String roomname;
	
}
