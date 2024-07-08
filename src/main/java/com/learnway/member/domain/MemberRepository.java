package com.learnway.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
// Member 관련 리포지토리 인터페이스
@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
    
	Optional<Member> findByMemberId(String memberId);
	List<Member> findByMemberNameContainingIgnoreCase(String memberId);
}
