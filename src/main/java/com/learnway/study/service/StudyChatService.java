package com.learnway.study.service;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learnway.member.domain.Member;
import com.learnway.member.domain.MemberRepository;
import com.learnway.study.domain.ChatMessage;
import com.learnway.study.domain.ChatRoom;
import com.learnway.study.domain.Study;
import com.learnway.study.domain.StudyChatRepository;
import com.learnway.study.dto.ChatRoomDto;

@Service
public class StudyChatService {

	@Autowired
	private StudyChatRepository studyChatRepository;
	@Autowired
	private MemberRepository memberRepository;
	
	//postId로 ChatRoomId 조회
	public List<ChatRoom> chatRoomId(int postId) {
	
		return studyChatRepository.findByStudyPostid(postId);
	}
	
	
	//채팅방 참여 메서드
//	public void joinChatRoom(Principal principal) {
//		
//		Member member = memberRepository.findByMemberId(principal.getName())
//	            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + principal.getName()));
//		
//		ChatRoom room = ChatRoom.builder().roomname(dto.getRoomname())
//				.study(study).member(member).build();
//	}
	
	//채팅방 생성 메서드
	public ChatRoom chatRoomCreate(ChatRoomDto dto,Study study,Principal principal) {
		
		Member member = memberRepository.findByMemberId(principal.getName())
	            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + principal.getName()));
		
		ChatRoom room = ChatRoom.builder().roomname(dto.getRoomname())
				.study(study).member(member).build();
		System.out.println(room.getRoomname() + "룸이름");
		
		return studyChatRepository.save(ChatRoom.builder().roomname(dto.getRoomname())
				.study(study).member(member).build());
	}
	
	
	
	//채팅 보관 메서드
	public ChatMessage storechat(ChatRoomDto dto,Principal principal) {
		
		//멤버값 넣을예정 임시로 1로지정
		Member member = memberRepository.findById( (long) 1)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + "1"));
		
		ChatRoom chatRoom = studyChatRepository.findById(dto.getRoomId())
				.orElseThrow(()-> new IllegalArgumentException("채팅방아이디  " + "1"));
		
		ChatMessage msg = ChatMessage.builder().msg(dto.getMessage()).member(member)
										       .chatroom(chatRoom)
											   .datetime(dto.getDatetime()).build();
		return msg;
	}
}
