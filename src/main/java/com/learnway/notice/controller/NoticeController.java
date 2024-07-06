package com.learnway.notice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learnway.global.exceptions.DataNotExeption;
import com.learnway.member.domain.Member;
import com.learnway.member.domain.MemberRepository;
import com.learnway.notice.domain.Notice;
import com.learnway.notice.dto.NoticeDto;
import com.learnway.notice.service.NoticeService;


@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Value("C:\\learway\\img\\notice")
	private String uploadPath;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private MemberRepository memberRepository;
	
	
	//공지사항 리스트 불러오기
	@GetMapping("/noticeList")
	public String noticeList(Model model,@RequestParam(value="page", defaultValue="0") int page
								,@RequestParam(value="keyword", required=false) String keyword
								,@RequestParam(value="category", required=false) String category) {
		
		Page<Notice> pageNotice = null;
		
		if (keyword == null || keyword.isEmpty()) {
	        if (category == null || category.isEmpty()) {
	            // 검색하지 않고 카테고리 선택하지 않았을 때
	            Pageable pageable = PageRequest.of(page, 10);
	            pageNotice = noticeService.noticeList(pageable);
	        } else {
	            // 검색하지 않고 카테고리 선택했을 때
	            Pageable pageable = PageRequest.of(page, 10);
	            pageNotice = noticeService.noticeCategoryList(pageable, category);
	        }
	    } else {
	        // 검색했을 때
	        Pageable pageable = PageRequest.of(page, 10);
	        pageNotice = noticeService.noticeSearchList(pageable, keyword);
	    }
		
		Page<Notice> priNotice;
		Pageable pri = PageRequest.of(page, 3);
		priNotice = noticeService.priNoticeList(pri);
		
		model.addAttribute("notice",pageNotice);
		model.addAttribute("priNotice",priNotice);
		
		
		return "/notice/noticeListView";
	}
	
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
	@PostMapping("/write")
	public String noticeWrtie(NoticeDto dto,@RequestParam("comFile") MultipartFile[] files,
							  Principal principal) {
		
		String memberId = dto.getMemberId();
		Optional<Member> member = memberRepository.findByMemberId(memberId);
		String NoticeImgUname = null;
		String NoticePath = null;
		
		//파일 업로드 처리
		for(MultipartFile comfile : files) {
			
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
		noticeService.write(dto,member);
		
		return "redirect:/notice/noticeList";
	}
	
	
	//글 상세페이지
	@GetMapping("/detail/{noticeId}")
	public String detail(@PathVariable("noticeId") Long noticeId,NoticeDto dto,Model model) throws DataNotExeption {
		
		//넘어온 아이디값으로 맞는 게시물 정보 찾기 
		dto = noticeService.findDetail(noticeId);
		model.addAttribute("notice",dto);
		
		return "/notice/noticeDetailView";
	}
	
	//글수정 페이지
	@GetMapping("/rewriteView/{noticeId}")
	public String rewriteView(@PathVariable("noticeId") Long noticeId,Model model,Principal principal) throws DataNotExeption{
		NoticeDto dto = noticeService.findDetail(noticeId);
		model.addAttribute("notice",dto);
		
		return "/notice/noticeReView";
	}
	
	//글 수정
	@PostMapping("/rewrite/{noticeId}")
	public String postMethodName( @ModelAttribute NoticeDto dto,@RequestParam("comFile") MultipartFile[] files) {
		
//		if(!comDTO.getMemberId().equals(principal.getName())){
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정 권한이 없습니다.");
//		}
		String noticeImgPath = dto.getNoticeImgPath();
		String noticeImgUname = dto.getNoticeImgUname();

		if(files != null && files.length > 0 && !files[0].isEmpty()) {
			MultipartFile File = files[0];
			
			//이미지 파일만 업로드 가능
			if(File.getContentType().startsWith("image")) {
				// 이미 기존 저장된 이미지가 있다면 기존 파일 삭제
	            if (noticeImgUname != null && !noticeImgUname.isEmpty()) {
	                Path oldFilePath = Paths.get(uploadPath, dto.getNoticeImgPath(), dto.getNoticeImgUname());
	                try {
	                	Files.deleteIfExists(oldFilePath);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
			     }
				
			//실제 파일 이름 IE나 Edge는 전체 경로가 들어옴
			String NoticeImgOname = File.getOriginalFilename();
			//마지막 백슬래시의 위치를 찾고 그 다음 글자부터(+1) 추출
			String fileName = NoticeImgOname.substring(NoticeImgOname.lastIndexOf("\\")+1);
				
			//파일 확장자 추출
			String fileExtension = fileName.substring(fileName.lastIndexOf("."));
			
			//날짜 폴더 생성
			noticeImgPath = noticeService.makeFolder();
			
			String uuid = UUID.randomUUID().toString();
			noticeImgUname = uuid+fileExtension;
			
			try {
				File.transferTo(Paths.get(uploadPath,noticeImgPath,noticeImgUname));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		  }
		}else {//이미지 수정하는 if문 끝 & 새로운 파일을 업로드하지 않은 경우, 기존 파일명과 경로를 그대로 유지
			dto.setNoticeImgPath(noticeImgPath);
			dto.setNoticeImgUname(noticeImgUname);	
		}
		
		dto.setNoticeId(dto.getNoticeId());
		dto.setPriority(dto.isPriority());
		noticeService.rewrite(dto);
		
		return "redirect:/notice/detail/" + dto.getNoticeId();
	}
	
	//글 삭제하기
	@GetMapping("delete/{noticeId}")
	public String delete(@PathVariable("noticeId") Long noticeId,Principal principal) throws DataNotExeption {
		
		NoticeDto dto = noticeService.findDetail(noticeId);//멤버아이디들어가야함
		noticeService.delete(dto);
		
		return "redirect:/notice/noticeList";
	}
	

}
