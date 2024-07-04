package com.learnway.study.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnway.study.dto.CorrectCheckDto;
import com.learnway.study.dto.StudyReplyDto;
import com.learnway.study.dto.StudyReplyResponseDto;
import com.learnway.study.service.StudyCorrectService;
import com.learnway.study.service.StudyReplyService;

@RestController
@RequestMapping("/api/member")
public class StudyRestController {

	@Autowired
	private StudyCorrectService studyCorrectService;
	@Autowired
	private StudyReplyService studyReplyService;
	
	
	@PostMapping("/correct")
	public void correct(@RequestBody CorrectCheckDto dto,Principal principal) {
		System.out.println("진입");
		System.out.println(dto.getStatus() + "상태값");
		System.out.println(dto.getRoomId() + "채팅방번호");
		System.out.println(dto.getPostId() + "게시글번호");
		
		studyCorrectService.updateStatus(dto,principal);
		
	}
	
	
	//댓글 추가 메서드
	@PostMapping("/replyadd")
	public List<StudyReplyResponseDto> replyAdd(@RequestBody StudyReplyDto dto,Principal principal) {
		
		
//			System.out.println(e
		
	        //댓글입력
	        studyReplyService.addReply(dto,principal);
	        
	        //추가된 댓글리스트 출력
	        List<StudyReplyResponseDto> list = studyReplyService.replyList(dto);
	        
	        return list;
	}
	
}
