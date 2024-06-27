package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ReservationDTO;
import com.example.demo.dto.UserInfoDTO;
import com.example.demo.repository.Consultant;
import com.example.demo.repository.ConsultantRepository;
import com.example.demo.repository.ReservationEntity;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.member.MemberEntity;
import com.example.demo.repository.member.MemberRepository;

@Service
public class ReservationService {
	
	private final ReservationRepository reservationRepository;
    
	@Autowired
    private ConsultantRepository consultantRepository;
	
	@Autowired
	private MemberRepository memberRepository;
    
	@Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }
	
    
    // 예약 저장 서비스
    public ReservationEntity save(ReservationEntity reservation) {
        return reservationRepository.save(reservation);
    }
    
   //??
//	public List<ReservationEntity> getAllReservations() {
//		return reservationRepository.findAll();	
//	}
	
	
	//사이드바에서 상담하기 버튼눌러서 모달창에 보여줄 상담사리스트 조회
    public List<Consultant> getAllConsultants() {
        return consultantRepository.findAll();
    }
    
    //메인->모달->특정상담사예약하기->컨트롤러에서 넘어온 특정상담사 정보를 모달창에 전달하기위한 조회 서비스메소드
    public Optional<Consultant> findByConsultants(Long id) {
        return consultantRepository.findById(id);
    }
    
    // 모달창에서 넘어와 로그인한유저 예약일정 가져오기
    public List<ReservationEntity> getReservationsByMemberId(Long memberId) {
        return reservationRepository.findBymember_id(memberId);
    }

    // 모달창에서 넘어와 특정 상담사 예약일정 가져오기
    public List<ReservationEntity> getReservationsByConsultantId(Long consultant) {
    	return reservationRepository.getReservationsListByCounselor_id(consultant);
    }
    
    // 예약취소 메소드
	public void deleteById(Long deleteId) {
		reservationRepository.deleteById(deleteId);
	}

	// 예약취소 하기전 취소알리주기위하여 정보조회 메소드
	public Optional<ReservationEntity> findById(Long deleteId) {
		return reservationRepository.findById(deleteId);
	}
	
	//상담사 예약 페이지 리스트 조회
	public List<ReservationDTO> getReservationsListByConsultantId(Long counselor_id) {
	    List<ReservationEntity> list = reservationRepository.getReservationsListByCounselor_id(counselor_id);
	    
	    List<ReservationDTO> reservationDTO = list.stream()
	        .map(reservation -> new ReservationDTO(
	            reservation.getId(),
	            reservation.getMember().getMemberId(), // Assuming getName() returns client name
	            reservation.getBookingStart().toString(),
	            reservation.getBookingEnd().toString()
	        ))
	        .collect(Collectors.toList());
	    
	    return reservationDTO;
	}
    
    //상담사 예약리스트에서 유저 정보 확인
    public UserInfoDTO getUserInfo(Long userId) {
        // 나중에 DB에서 정확한 정보 가져와야함
        Optional<MemberEntity> memberOptional = memberRepository.findById(userId);
        
        // memberOptional이 비어있을 경우를 처리해야 합니다.
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            return new UserInfoDTO(
                member.getId(),
                member.getMemberName(),
                member.getMemberEmail(),
                member.getMemberAge()
            );
        } else {
            // Optional이 비어있을 경우 처리 (예: 예외를 던지거나 null 반환 등)
            // 여기서는 예외를 던지는 방식으로 처리
            throw new NoSuchElementException("User not found with id: " + userId);
        }
    }


}
