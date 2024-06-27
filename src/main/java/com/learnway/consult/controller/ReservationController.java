package com.learnway.consult.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReservationDTO;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.dto.UserInfoDTO;
import com.example.demo.dto.member.MemberDetails;
import com.example.demo.repository.Consultant;
import com.example.demo.repository.ConsultantRepository;
import com.example.demo.repository.ReservationEntity;
import com.example.demo.repository.member.MemberEntity;
import com.example.demo.service.ReservationService;

@RestController
@RequestMapping("/api")
public class ReservationController {
	
    @Autowired
    private SseController sseController;
	
	@Autowired
    private ReservationService reservationService;
	
    @Autowired
    private ConsultantRepository consultantRepository;
	
    // 상담사페이지 예약리스트
    @GetMapping("/reservationsList")
    public List<ReservationDTO> getReservationsList(@RequestParam("counselor_id") Long counselor_id,Authentication authentication) {
        MemberEntity member = null;
        if (authentication != null && authentication.isAuthenticated()) {
            MemberDetails userDetails = (MemberDetails) authentication.getPrincipal();
            member = userDetails.getMember();
        }
        System.out.println("멤버pk값 : " + member.getId()); 
    	
    	return reservationService.getReservationsListByConsultantId(member.getId());
    }
    
    //상담사예약페이지에서 상담신청자 정보 조회
    @GetMapping("/users/{userId}")
    public UserInfoDTO users(@PathVariable("userId") Long userId) {
    	System.out.println("유저조회 들어옴");
    	return reservationService.getUserInfo(userId);
    }
    
    // main에서 모달창에 상담사 불러오기위한 메서드
    @GetMapping("/consultants")
    public List<Consultant> getConsultants() {
        return reservationService.getAllConsultants();
    }	
		
    
    // 모달창에서 넘어와 멤버개인의 예약일정 보여주기    
    @GetMapping("/myReservations")
    public List<ReservationEntity> getMyReservations(Authentication authentication) {
    	System.out.println("마이페이지에서 모달로 요청온 나의 상담리스트");
        MemberEntity member = null;
        if (authentication != null && authentication.isAuthenticated()) {
            MemberDetails userDetails = (MemberDetails) authentication.getPrincipal();
            member = userDetails.getMember();
        }
        System.out.println("멤버pk값 : " + member.getId()); 
    	
    	return reservationService.getReservationsByMemberId(member.getId());
    }
    
    // 모달창에서 넘어와 특정상담사 예약일정 보여주기    
    @GetMapping("/reservations")
    public List<ReservationEntity> getReservations(@RequestParam("consultant") Long consultant) {
    	System.out.println("특정상담사 예약일정");
    	return reservationService.getReservationsByConsultantId(consultant);
    }
    
    //예약취소메서드
    @DeleteMapping("/reservations/{deleteId}")
    public ResponseEntity<String> deleteReservations(@PathVariable("deleteId") Long deleteId) {
        System.out.println("deleteId : " + deleteId);
        try {
        	Optional<ReservationEntity> deleteList = reservationService.findById(deleteId);
            reservationService.deleteById(deleteId);
            
            // 알림보내기
            // 예약 생성 후 알림 발송
            String name = deleteList.get().getMember().getMemberName();
            Long consultantId = deleteList.get().getCounselor().getId();
            LocalDateTime startTime = deleteList.get().getBookingStart();

            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL_STANDALONE)
                .appendLiteral(" ")
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral("일 ")
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral("시")
                .toFormatter(Locale.KOREAN);

            String formattedDate = startTime.format(formatter);
            System.out.println("취소알림."); 
            System.out.println(formattedDate + " " + name + " 님의"); 
            System.out.println("예약이 취소되었습니다.");
            
            String message = "취소알림"+"<br/>" + formattedDate + " " + name + " 님의" +"<br/>" +
                             "예약이 취소되었습니다.";
            System.out.println("3번 상담사아이디 표시 : " + consultantId);
            
            sseController.sendNotificationToConsultant(consultantId, message);
            
            return ResponseEntity.ok("예약이 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("예약 취소 중 오류가 발생했습니다.");
        }
    }
    
    // 예약 추가하기
    @PostMapping("/reservations")
    public ResponseEntity<ReservationEntity> createReservation(@RequestBody ReservationRequest request, Authentication authentication) {
        System.out.println("request.getCounselor() : " + request.getCounselor());
        Optional<Consultant> optionalConsultant = consultantRepository.findById(request.getCounselor());
        if (!optionalConsultant.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        MemberEntity member = null;
        if (authentication != null && authentication.isAuthenticated()) {
            MemberDetails userDetails = (MemberDetails) authentication.getPrincipal();
            member = userDetails.getMember();
        }
        Consultant consultant = optionalConsultant.get();
        ReservationEntity reservation = new ReservationEntity();
        reservation.setMember(member);
        reservation.setCounselor(consultant);
        reservation.setReservationContent(request.getReservationContent());
        reservation.setBookingStart(request.getBookingStart());
        reservation.setBookingEnd(request.getBookingEnd());

        ReservationEntity savedReservation = reservationService.save(reservation);
        
        // 알림보내기
        // 예약 생성 후 알림 발송
        LocalDateTime startTime = request.getBookingStart();
        LocalDateTime endTime = request.getBookingEnd();
        
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL_STANDALONE)
                .appendLiteral(" ")
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral("일 ")
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral("시")
                .toFormatter(Locale.KOREAN);

            String startFormatter = startTime.format(formatter);
            String endFormatter = endTime.format(formatter);
        
        String message = "신규 예약이 있습니다."+"<br/>" + "예약자 성함 : " + reservation.getMember().getMemberName() +"<br/>" +
                         "시작 시간 : " + startFormatter +"<br/>" +"종료 시간 : " + endFormatter;
        sseController.sendNotificationToConsultant(reservation.getCounselor().getId(), message);

        return ResponseEntity.ok(savedReservation);
    }
}

