package com.learnway.notice.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learnway.notice.dto.NoticeDto;
import com.learnway.notice.service.NoticeService;

import ch.qos.logback.core.model.Model;

@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Value("C:\\learway\\img\\notice")
	private String uploadPath;
	
	@Autowired
	private NoticeService noticeService;
	
	
//	//공지사항 리스트 불러오기
//	@GetMapping("/NoticeList")
//	public String NoticeList(Model model,@RequestParam(value="page", defaultValue="0") int page) {
//		
//	}
	
	//글쓰기 폼
		@GetMapping("/writeView")
		public String communityWriteView(Principal principal,RedirectAttributes redirectAttributes ) {
			
//			if (principal == null || principal.getName().isEmpty()) {
//		        redirectAttributes.addFlashAttribute("errorMessage", "로그인을 하셔야 글쓰기를 할 수 있습니다.");
//		        return "redirect:/exhibitscape/community/communityList"; // 로그인 페이지로 리다이렉트
//		    }
			return "/notice/noticeWriteView";
		}
		
		
	//글쓰기
	@PostMapping("/Wrtie")
	public String noticeWrtie(NoticeDto dto,@RequestParam("comFile") MultipartFile[] comFiles,
								Principal principal) {
		
		String NoticeImgUname = null;
		String NoticePath = null;
		
		//파일 업로드 처리
		for(MultipartFile comfile : comFiles) {
			//이미지 파일만 업로드 가능 , 이미지가 아닐 경우 현재 반복을 건너뜀
			if(!comfile.getContentType().startsWith("image")) {
				continue; 
			}
			
		//실제 파일 이름 IE나 Edge는 전체 경로가 들어옴
		String NoticeImgOname = comfile.getOriginalFilename();
		//마지막 백슬래시의 위치를 찾고 그 다음 글자부터(+1) 추출
		String fileName = NoticeImgOname.substring(NoticeImgOname.lastIndexOf("\\")+1);
			
		//파일 확장자 추출
		String fileExtension = fileName.substring(fileName.lastIndexOf("."));
		
		//날짜 폴더 생성
		NoticePath = noticeService.makeFolder();
		
		String uuid = UUID.randomUUID().toString();
		NoticeImgUname = uuid+fileExtension;
		
		try {
			comfile.transferTo(Paths.get(uploadPath,NoticePath,NoticeImgUname));
        } catch (IOException e) {
            e.printStackTrace();
        }
		
			
		}//파일 업로드 처리 끝
		
		//dto.setMemberId(memberId);
		dto.setNoticeImgPath(NoticePath);
		dto.setNoticeImgUname(NoticeImgUname);
		
		return "redirect:/notice/NoticeWriteForm";
	}

}
