package com.learnway.study.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.learnway.study.domain.StudyProblem;
import com.learnway.study.domain.StudyProblemImg;
import com.learnway.study.domain.StudyProblemImgRepository;
import com.learnway.study.dto.StudyProblemImgDto;

@Service
public class StudyProblemImgService {
	
	@Value("C:\\learway\\img\\studyself")
	private String uploadPath;

	@Autowired
	private StudyProblemImgRepository studyProblemImgRepository;
	
	
	//해당문제 이미지값 조회
	public List<StudyProblemImg> problemImgPath(int problemid) {
	
		return studyProblemImgRepository.findByStudyProblemProblemid(problemid);
	}
	
	
	//문제이미지 업로드 메서드
	public void problemImgAdd(StudyProblemImgDto dto,MultipartFile[] files,int problemid) {
		for(MultipartFile file : files) {
			if(!file.isEmpty()&&file.getContentType().startsWith("image")) {
				String orgfile = file.getOriginalFilename();
				String filename = orgfile.substring(orgfile.lastIndexOf("/")+1);
				String storename = UUID.randomUUID().toString()+"_"+filename.substring(filename.lastIndexOf("."));
				try {
					File uploadPathFolder = new File(uploadPath);
					
					if(uploadPathFolder.exists()==false) {
						uploadPathFolder.mkdirs();
					}
					
					file.transferTo(Paths.get(uploadPath,storename));
					StudyProblemImg studyProblemImg = StudyProblemImg.builder().imgdir(uploadPath)
							  .imgpath(storename).correct(dto.getCorrect()).
						      studyProblem(StudyProblem.builder().problemid(problemid).build()).build();

					studyProblemImgRepository.save(studyProblemImg);
					
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	//문제수정 이미지업로드
	public void problemImgUpdate(StudyProblemImgDto dto,MultipartFile[] files,int problemid) {
		for(MultipartFile file : files) {
			if(!file.isEmpty()&&file.getContentType().startsWith("image")) {
				String orgfile = file.getOriginalFilename();
				String filename = orgfile.substring(orgfile.lastIndexOf("/")+1);
				String storename = UUID.randomUUID().toString()+"_"+filename.substring(filename.lastIndexOf("."));
				try {
					File uploadPathFolder = new File(uploadPath);
					
					if(uploadPathFolder.exists()==false) {
						uploadPathFolder.mkdirs();
					}
					
					file.transferTo(Paths.get(uploadPath,storename));
					
					List<StudyProblemImg> list = studyProblemImgRepository.findByStudyProblemProblemid(problemid);
					int pbimgId = 0;
					for(StudyProblemImg a : list) {
						pbimgId = a.getPbimgid();
					}
					
					StudyProblemImg studyProblemImg = StudyProblemImg.builder().pbimgid(pbimgId)
							.imgdir(uploadPath).imgpath(storename).correct(dto.getCorrect())
							.studyProblem(StudyProblem.builder().problemid(problemid).build()).build();
					
					studyProblemImgRepository.save(studyProblemImg);
					
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
}
