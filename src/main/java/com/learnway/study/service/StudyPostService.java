package com.learnway.study.service;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.learnway.member.domain.Member;
import com.learnway.member.domain.MemberRepository;
import com.learnway.member.service.CustomUserDetails;
import com.learnway.member.service.CustomUserDetailsService;
import com.learnway.study.domain.Study;
import com.learnway.study.domain.StudyRepository;
import com.learnway.study.dto.StudyDto;

@Service
public class StudyPostService {
	
	@Autowired
	private StudyRepository studyRepository;
	@Autowired
	private MemberRepository memberRepository;
	
	
	public List<Study> findAll() {
        return studyRepository.findAll(Sort.by(Sort.Direction.DESC,"postid"));
    }

	
	public Page<Study> getBoardList(Pageable pageable) {
		
//		return studyRepository.findAll(pageable);
		 Sort sort = Sort.by(Sort.Direction.DESC, "postid");
		 Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		 return studyRepository.findAll(sortedPageable);
	}
	
	
	//게시글 작성(게시글,지도,스터디채팅방,태그,문제 트랜젝션처리)
	//현재메서드는 게시글작성 및  return 값으로는 작성중인 potsId값 반환
	public Study boardadd(StudyDto dto,Principal principal) {
		

		//로그인한 값 가져와 수정예정
		Member member = memberRepository.findByMemberId(principal.getName())
	            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + principal.getName()));
		
		
//		memberRepository.findByMemberId(principal.getName()).get();
		System.out.println("멤버아이디 " + memberRepository.findByMemberId(principal.getName()).get());
		//멤버엔티티 연결해야함
		
		Study study = Study.builder().title(dto.getTitle())
									       .content(dto.getContent())
									       .viewcount("0")
									       .startdate(dto.getStartdate())
									       .enddate(dto.getEnddate())
									       .isjoin((byte) dto.getIsjoin()).member(memberRepository.findByMemberId(principal.getName()).get()).build();
		
	    
		return studyRepository.save(study);
		
		
	}
	


}
