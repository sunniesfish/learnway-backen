package com.learnway.study.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.learnway.study.domain.ChatMessageRepository;
import com.learnway.study.domain.StudyChatRepository;
import com.learnway.study.dto.ChatRoomDto;
import com.learnway.study.dto.CorrectCheckDto;
import com.learnway.study.service.StudyChatService;



@Controller
public class StudyChatController {

	@Autowired
	StudyChatRepository studyChatRepository;
	@Autowired
	ChatMessageRepository chatMessageRepository;
	@Autowired
	StudyChatService studyChatService;
	@Autowired
	private  SimpMessagingTemplate template;
	
	
	@GetMapping(value="/createRoom")
	public String createRoom() {
		return "study/createRoom";
	}
	
	@RequestMapping(value="/joinRoom",method= {RequestMethod.GET,RequestMethod.POST})
	public String addRoom(ChatRoomDto dto,
			Model model,Principal principal,CorrectCheckDto cdto) {
		
		//채팅방 이름저장 ->수정필요 (FK로 게시글id,맴버id 필요함)
//		studyChatService.chatRoomCreate(dto,0);
		
		System.out.println("채팅방입장");
		System.out.println(dto.getRoomId() + " : 룸아이디");
		System.out.println(dto.getRoomname() + " : 룸이름");
		System.out.println(principal.getName() + " : 로그인 사용자");
		model.addAttribute("name",1); //채팅방-멤버테이블 가져올값
		model.addAttribute("id",1); //채팅방-멤버테이블 가져올값
		model.addAttribute("roomId",dto.getRoomId()); //수정해야됨
		model.addAttribute("roomname","테스트");
		//채팅방 저장
		return "/study/mychat";
	}
	
	@MessageMapping(value="/chat/enter")
	public void chatenter(ChatRoomDto dto,Principal principal) {
//		dto.setName(principal.getName());
		dto.setMessage(dto.getName() + "님이 채팅방에 입장하셨습니다.");
		template.convertAndSend("/sub/chat/room/"+dto.getRoomId(),dto);
	}
	
	@MessageMapping(value="/chat/message")
	public void message(ChatRoomDto dto,Principal principal) {
		System.out.println(dto.getName() + "보낸사람");
		
		
		LocalDateTime datetime = LocalDateTime.parse(dto.getDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		dto.setDatetime(datetime);
		
		System.out.println(dto.getRoomId()+ "채팅방 아이디");
		chatMessageRepository.save(studyChatService.storechat(dto,principal));
		
		System.out.println("채팅 저장됨");
		template.convertAndSend("/sub/chat/room/"+dto.getRoomId(),dto);

	}
	
	
}
