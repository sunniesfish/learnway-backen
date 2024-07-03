package com.learnway.study.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learnway.member.domain.Member;
import com.learnway.member.domain.MemberRepository;
import com.learnway.study.domain.CorrectCheck;
import com.learnway.study.domain.CorrectCheckRepository;
import com.learnway.study.domain.Study;
import com.learnway.study.domain.StudyRepository;
import com.learnway.study.dto.CorrectCheckDto;

@Service
public class StudyCorrectService {

	
	@Autowired
	private CorrectCheckRepository correctCheckRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private StudyRepository studyRepository;
	
	
	
	public void updateStatus(CorrectCheckDto dto,Principal principal) {
		
		//멤버넘어올시 변경
		Member member = memberRepository.findByMemberId(principal.getName())
	            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + principal.getName()));
		
		Study study = studyRepository.findById(dto.getPostId())
				.orElseThrow(() -> new IllegalArgumentException("게시글 번호 " + dto.getPostId()));
		
		CorrectCheck ck = CorrectCheck.builder().status(dto.getStatus())
							  .member(member).study(study).build();
		
		correctCheckRepository.save(ck);
	}
}
