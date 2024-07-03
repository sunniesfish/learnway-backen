package com.learnway.study.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.learnway.study.domain.Study;
import com.learnway.study.domain.StudyTag;
import com.learnway.study.domain.StudyTagRepository;
import com.learnway.study.dto.StudyTagDto;

@Service
public class StudyTagService {

	@Autowired
	private StudyTagRepository studyTagRepository;
	
	//모든태그 조회
	public List<StudyTag> findAllTag() {
		
		return studyTagRepository.findAll();
	}
	
	
	
	//게시글작성시 태그저장
	public void createTag(StudyTagDto studyTagDto,Study study) {
		
		StudyTag tag = StudyTag.builder().tag(studyTagDto.getTag()).study(study).build();
		studyTagRepository.save(tag);
	}
	
	//게시글 태그 수정
	public void updateTag(StudyTagDto studyTagDto,Study study) {
		
		StudyTag tag = StudyTag.builder().tag(studyTagDto.getTag()).study(study).build();
		studyTagRepository.save(tag);
	}
	
	
	
	//게시글조회시 저장된태그값 read
	public List<StudyTag> findTag(int postid) {
		return studyTagRepository.findByStudyPostid(postid);
	}
}
