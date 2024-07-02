package com.learnway.study.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnway.member.domain.Member;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
	
	List<ChatRoomMember> findByMember_MemberId(String memberId);
}
