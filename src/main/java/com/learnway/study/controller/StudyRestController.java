package com.learnway.study.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnway.study.dto.CorrectCheckDto;
import com.learnway.study.service.StudyCorrectService;

@RestController
@RequestMapping("/api/member")
public class StudyRestController {

	@Autowired
	private StudyCorrectService studyCorrectService;
	
	@PostMapping("/correct")
	public void correct(@RequestBody CorrectCheckDto dto) {
		System.out.println("진입");
		System.out.println(dto.getStatus() + "상태값");
		System.out.println(dto.getRoomId() + "채팅방번호");
		System.out.println(dto.getPostId() + "게시글번호");
		
		studyCorrectService.updateStatus(dto);
		
	}
	
}
