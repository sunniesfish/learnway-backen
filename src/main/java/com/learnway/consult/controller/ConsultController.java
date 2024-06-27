package com.learnway.consult.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learnway.consult.domain.Consultant;
import com.learnway.consult.service.ConsultantDetails;
import com.learnway.consult.service.ReservationService;
import com.learnway.member.domain.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ConsultController {
	@Autowired
	private ReservationService reservationService;
	
	//유저 마이페이지에 기생할 유저 예약리스트 확인하는모달
	@GetMapping("/reservation")
	public String reservation(Authentication authentication,Model model) {
		
    	Long memberId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Member userDetails = (Member) authentication.getPrincipal();
            memberId = userDetails.getId();
            System.out.println(memberId);
        }
        model.addAttribute("memberId", memberId);

		return "consult/reservation";
	}
	//임시메인페이지 나중에 메인페이지가 마이페이지이기때문에 여기 추가되는 사이드바에 기생할예정
	@GetMapping("/main")
	public String main(Authentication authentication,Model model) {
    	String memberId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            memberId = userDetails.getUsername();
            System.out.println(memberId);
        }
        model.addAttribute("memberId", memberId);
		return "main";
	}
	
	//모달창에서 상담사 예약하기누르면 처리하는 메소드
	@GetMapping("/reservationBoard")
	public String reservationBoard(@RequestParam("consultant") Long id,Model model) {
		
		System.out.println("메인상담사리스트모달로 예약하기로 클릭시 상담사에 관한 정보를 모달창에 전달하기위한 메소드 : 상담사PK값 " + id);
		Optional<Consultant> consultantInfo = reservationService.findByConsultants(id);
		model.addAttribute("consultantName", consultantInfo.get().getName());
		model.addAttribute("consultantSubject", consultantInfo.get().getSubject());
		model.addAttribute("consultantId", consultantInfo.get().getId());
		
		return "consult/reservationBoard";
	}
	//상담사 페이지
	@GetMapping("/consultant")
	public String consultantPage(Authentication authentication,Model model) {
		//현제 상담사 pk값 1  사용중  상담사 로그인 구현되지않음 (pk1번은 박준영상담사 심리상담 )
	  	Long consultantId = null;
        if (authentication != null && authentication.isAuthenticated()) {
        	ConsultantDetails userDetails = (ConsultantDetails) authentication.getPrincipal();
            consultantId = userDetails.getId();
            System.out.println("consultantId : "+consultantId);
        }
		model.addAttribute("counselor_id", consultantId);
		return "consult/consultant";
	}

	//로그인한사용자의 예약리스트페이지 (지금 사용중이지않음 마이페이지에 모달창으로 대체)
	@GetMapping("/userReservation")
	public String userReservationPage() {
		return "consult/userReservationLIst";
	}
	
	@GetMapping("/video")
	public String video() {
		
		return "/consult/video";
	}
	
	//임시 멤버 관리
	@GetMapping("/login")
	public String loginP() {
		
		return "/member/login";
	}
	
	//뷰화면 테스트
	@GetMapping("/test")
	public String test() {
		
		return "/test";
	}
	
	//로그인한 세션 정보 확인
	   @GetMapping("/checkSession")
	    public String checkSession(HttpServletRequest request) {
	        HttpSession session = request.getSession(false); // false로 설정하여 새로운 세션이 생성되지 않도록 함
	        if (session != null) {
	            Long loggedInConsultantId = (Long) session.getAttribute("loggedInConsultantId");
	            if (loggedInConsultantId != null) {
	                return "현재 로그인한 상담사 ID: " + loggedInConsultantId;
	            } else {
	                return "상담사가 로그인하지 않은 상태입니다.";
	            }
	        } else {
	            return "세션에 로그인 정보가 없습니다.";
	        }
	    }
	   
		//로그인한 세션 정보 확인
	   @GetMapping("/loginCheck")
	    public String loginCheck(Authentication authentication) {
		   
		  	String memberId = null;
	        if (authentication != null && authentication.isAuthenticated()) {
	        	Member userDetails = (Member) authentication.getPrincipal();
	            memberId = userDetails.getMemberName();
	            System.out.println("현재 로그인 아이디(멤버) : "+  memberId);
	        }
		   
	        if (memberId != null) {
	            if (memberId != null) {
	                return "현재 로그인한 멤버 ID: " + memberId;
	            } else {
	                return "상담사가 로그인하지 않은 상태입니다.";
	            }
	        } else {
	            return "세션에 로그인 정보가 없습니다.";
	        }
	    }
}
