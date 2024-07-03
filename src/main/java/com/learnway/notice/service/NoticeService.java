package com.learnway.notice.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NoticeService {
	
	@Value("C:\\learway\\img\\notice")
	private String uploadPath;
	
	//날짜 폴더 생성
	public String makeFolder() {
		
		String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String folderPath = str;
		
		//make folder 
		File uploadPathFolder = new File(uploadPath,folderPath);
		
		if(uploadPathFolder.exists()==false) {
			uploadPathFolder.mkdirs();
		}
		
		return folderPath;
	}

}
