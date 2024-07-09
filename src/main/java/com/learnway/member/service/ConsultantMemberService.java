package com.learnway.member.service;

import com.learnway.consult.domain.Consultant;
import com.learnway.global.exceptions.S3Exception;
import com.learnway.global.service.S3ImageService;
import com.learnway.member.dto.ConsultantJoinDTO;
import com.learnway.consult.domain.ConsultantRepository;
import com.learnway.member.domain.MemberRepository;
import com.learnway.member.dto.ConsultantUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ConsultantMemberService {

    private final ConsultantRepository consultantRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3ImageService s3ImageService;

    private final String imgDir = "images/consultant";

    public void joinConsultant(ConsultantJoinDTO consultantJoinDTO) {
        // member / consultant 테이블에서 동일한 ID 조회
        if (consultantRepository.findByConsultantId(consultantJoinDTO.getUsername()).isPresent() ||
                memberRepository.findByMemberId(consultantJoinDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }
        // 패스워드, 패스워드 컨펌 일치 확인
        if (!consultantJoinDTO.getPassword().equals(consultantJoinDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 이미지 저장
        String imagePath;
        try {
            imagePath = s3ImageService.upload(consultantJoinDTO.getImage(),imgDir);
        } catch (S3Exception e) {
            throw new RuntimeException(e);
        }

        Consultant consultant = Consultant.builder()
                .consultantId(consultantJoinDTO.getUsername())
                .password(bCryptPasswordEncoder.encode(consultantJoinDTO.getPassword()))
                .name(consultantJoinDTO.getName())
                .subject(consultantJoinDTO.getSubject())
                .description(consultantJoinDTO.getDescription())
                .imageUrl(imagePath)
                .role("COUNSELOR")
                .build();

        consultantRepository.save(consultant);
    }

    public ConsultantUpdateDTO getConsultantById(Long id) {
        Consultant consultant = consultantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컨설턴트입니다."));
        ConsultantUpdateDTO consultantUpdateDTO = new ConsultantUpdateDTO();
        consultantUpdateDTO.setUsername(consultant.getConsultantId());
        consultantUpdateDTO.setName(consultant.getName());
        consultantUpdateDTO.setSubject(consultant.getSubject());
        consultantUpdateDTO.setDescription(consultant.getDescription());
        consultantUpdateDTO.setImageUrl(consultant.getImageUrl());
        return consultantUpdateDTO;
    }

    public void updateConsultant(ConsultantUpdateDTO consultantUpdateDTO) {
        Consultant consultant = consultantRepository.findByConsultantId(consultantUpdateDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컨설턴트입니다."));
        if (consultantUpdateDTO.getPassword() != null && !consultantUpdateDTO.getPassword().isEmpty()) {
            consultant.setPassword(bCryptPasswordEncoder.encode(consultantUpdateDTO.getPassword()));
        }
        consultant.setName(consultantUpdateDTO.getName());
        consultant.setSubject(consultantUpdateDTO.getSubject());
        consultant.setDescription(consultantUpdateDTO.getDescription());
        if (consultantUpdateDTO.getImage() != null && !consultantUpdateDTO.getImage().isEmpty()) {
            try {
                String imagePath = s3ImageService.upload(consultantUpdateDTO.getImage(),imgDir);
                s3ImageService.deleteImageFromS3(consultant.getImageUrl());
                consultant.setImageUrl(imagePath);
            } catch (S3Exception e) {
                throw new RuntimeException(e);
            }
        }
        consultantRepository.save(consultant);
    }

//    private String saveImage(MultipartFile image) throws IOException {
//        if (image == null || image.isEmpty()) {
//            return "/img/member/member-default.png"; // 기본 이미지 경로
//        }
//        // 이름 위에 현재 시간 작성하여 파일 구분
//        String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
//        Path imagePath = Paths.get(uploadPath, filename);
//        Files.createDirectories(imagePath.getParent());
//        Files.copy(image.getInputStream(), imagePath);
//
//        return filename;
//    }

    // 이미지 삭제 메서드
//    private void deleteImage(String imagePath) {
//        if (imagePath != null && !imagePath.equals("/img/member/member-default.png")) {
//            try {
//                Path filePath = Paths.get(uploadPath).resolve(imagePath);
//                Files.deleteIfExists(filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}