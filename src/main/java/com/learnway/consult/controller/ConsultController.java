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
import com.learnway.member.service.CustomUserDetails;

@Controller
//@RequestMapping("/consult")
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
	@GetMapping("/consult/consultant")
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
	
	//상담사예약페이지 입장하기클릭시 엔드포인트
	@GetMapping("/consult/video")
	public String consultVideo(Authentication authentication,Model model) {
		System.out.println("상담사 화상상담들어옴");
	  	String consultantId = null;
        if (authentication != null && authentication.isAuthenticated()) {
        	ConsultantDetails userDetails = (ConsultantDetails) authentication.getPrincipal();
            consultantId = userDetails.getConsultant().getConsultantId();
            System.out.println("getUsername : "+ consultantId);
            model.addAttribute("connectId", consultantId);
        }
		return "/consult/video";
	}
	
	//사이드바 예약리스트에서 입장하기 클릭시 엔드포인트
	@GetMapping("/member/video")
	public String memberVideo(Authentication authentication,Model model) {
		System.out.println("멤버 화상상담들어옴");
	  	String memberId = null;
        if (authentication != null && authentication.isAuthenticated()) {
        	CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        	memberId = userDetails.getMember().getMemberId();
            System.out.println("getUsername : "+ memberId);
            model.addAttribute("connectId", memberId);
        }
		return "/consult/video";
	}

	//뷰화면 테스트
	@GetMapping("/test")
	public String test(Authentication authentication,Model model) {
//    	String memberId = null;
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            memberId = userDetails.getUsername();
//            System.out.println(memberId);
//        }
//        model.addAttribute("memberId", memberId);
		
		return "/page";
	}
	
	//로그인한 세션 정보 확인
	   @GetMapping("/checkSession")
	    public String checkSession(Authentication authentication) {
		   ConsultantDetails consultant = (ConsultantDetails) authentication.getPrincipal();
		   //Long consultantId = consultant.getId();
		   System.out.println("이게머야?1 "+ consultant.getId());
		   System.out.println("이게머야?2 "+ authentication.getName());
		   System.out.println("이게머야?3 "+ authentication.getAuthorities());
		   System.out.println("이게머야?4 "+ authentication.getDetails());
		  // HttpSession session = request.getSession(false); // false로 설정하여 새로운 세션이 생성되지 않도록 함
	        if (consultant != null) {
	        	Long consultantId = consultant.getId();
	            if (consultantId != null) {
	                return "현재 로그인한 상담사 ID: " + consultantId;
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
