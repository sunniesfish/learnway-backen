package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.Consultant;
import com.example.demo.repository.ConsultantRepository;

@Service
public class ConsultantService {

    @Autowired
    private ConsultantRepository consultantRepository;

    // Consultant ID로 상담사 정보 조회
    public Consultant getConsultantById(Long consultantId) {
        return consultantRepository.findById(consultantId)
                                   .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));
    }
}
 
