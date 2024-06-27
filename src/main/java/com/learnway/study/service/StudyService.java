package com.learnway.study.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.learnway.study.domain.Study;
import com.learnway.study.dto.ChatRoomDto;
import com.learnway.study.dto.StudyDto;
import com.learnway.study.dto.StudyProblemDto;
import com.learnway.study.dto.StudyProblemImgDto;
import com.learnway.study.dto.StudyTagDto;



//스터디게시글 트랜젝션 처리 서비스
@Service
public class StudyService {

	
	private StudyPostService studyPostService;
	private StudyChatService studyChatService;
	private StudyTagService studyTagService;
	private StudyProblemService studyProblemService;
	private StudyProblemImgService studyProblemImgService;
	
	@Autowired
	public StudyService(StudyPostService studyPostService, StudyChatService studyChatService,
						StudyTagService studyTagService,StudyProblemService studyProblemService
						,StudyProblemImgService studyProblemImgService) {
		this.studyPostService = studyPostService;
		this.studyChatService = studyChatService;
		this.studyTagService = studyTagService;
		this.studyProblemService = studyProblemService;
		this.studyProblemImgService = studyProblemImgService;
	}
	
	
	
	@Transactional
	public void crateBoard(StudyDto dto,ChatRoomDto chatRoomDto,StudyTagDto studyTagDto,
						StudyProblemDto studyProblemDto,StudyProblemImgDto studyProblemImgDto
						,MultipartFile[] files) {
		
		System.out.println("게시글생성");
		//게시글 생성
		Study study = studyPostService.boardadd(dto);
		int postid = study.getPostid();
		
		//채팅방 생성
		studyChatService.chatRoomCreate(chatRoomDto, study);
		//태그값 저장
		studyTagService.createTag(studyTagDto, study);
		//문제 저장
		int problemid = studyProblemService.problemAdd(studyProblemDto,postid);
		
		System.out.println(problemid + "해당문제아이디");
		
		//문제이미지 저장
		studyProblemImgService.problemImgAdd(studyProblemImgDto,files,problemid);
		
		
	}
}
