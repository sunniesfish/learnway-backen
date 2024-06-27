package com.learnway.study.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StudyChatRepository extends JpaRepository<ChatRoom, Integer> {

	List<ChatRoom> findByStudyPostid(int postid);
	
	
}
