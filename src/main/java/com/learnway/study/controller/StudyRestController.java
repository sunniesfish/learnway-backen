package com.learnway.study.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnway.study.domain.Study;
import com.learnway.study.dto.CorrectCheckDto;
import com.learnway.study.dto.StudyDto;
import com.learnway.study.dto.StudyReplyDto;
import com.learnway.study.dto.StudyReplyResponseDto;
import com.learnway.study.service.StudyCorrectService;
import com.learnway.study.service.StudyPostService;
import com.learnway.study.service.StudyReplyService;

@RestController
@RequestMapping("/api")
public class StudyRestController {

	@Autowired
	private StudyCorrectService studyCorrectService;
	@Autowired
	private StudyReplyService studyReplyService;
	@Autowired
	private StudyPostService studyPostService;
	
	
	@PostMapping("/member/correct")
	public void correct(@RequestBody CorrectCheckDto dto,Principal principal) {
		System.out.println("진입");
		System.out.println(dto.getStatus() + "상태값");
		System.out.println(dto.getRoomId() + "채팅방번호");
		System.out.println(dto.getPostId() + "게시글번호");
		
		studyCorrectService.updateStatus(dto,principal);
		
	}
	
	
	//댓글 추가 메서드
	@PostMapping("/member/replyadd")
	public List<StudyReplyResponseDto> replyAdd(@RequestBody StudyReplyDto dto,Principal principal) {
		
		
		
	        //댓글입력
	        studyReplyService.addReply(dto,principal);
	        
	        //추가된 댓글리스트 출력
	        List<StudyReplyResponseDto> list = studyReplyService.replyList(dto);
	        
	        return list;
	}
	
	//검색 메서드
	@PostMapping("/study/searchList")
	public List<Study> searchBoardList(@RequestBody StudyDto dto) {
		
		System.out.println(dto.getTitle());
		return studyPostService.searchBoardList(dto);
	}
	
}
