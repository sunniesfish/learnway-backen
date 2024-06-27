package com.learnway.consult.service;

import com.learnway.consult.domain.Consultant;
import com.learnway.consult.domain.ConsultantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Counselor 멀티 로그인 테스트 파일
@RequiredArgsConstructor
@Service
public class ConsultantService  implements UserDetailsService {
    private final ConsultantRepository consultantRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Consultant consultant = consultantRepository.findByConsultantId(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 카운셀러 없음"));

        return new ConsultantDetails(consultant);
    }
    // Consultant ID로 상담사 정보 조회
    public Consultant getConsultantById(Long consultantId) {
        return consultantRepository.findById(consultantId)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));
    }
}
