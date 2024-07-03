package com.learnway.global.service;

import com.learnway.global.domain.*;
import com.learnway.global.dto.MasterDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// 기준 정보(Material/Subject/StudyWay)를 관리하는 서비스 클래스
@Service
@RequiredArgsConstructor
public class MasterDataService {

    private final MaterialRepository materialRepository;
    private final StudywayRepository studywayRepository;
    private final SubjectRepository subjectRepository;

    // 기준 정보 추가
    public void addMasterData(MasterDataDTO masterDataDTO) {
        switch (masterDataDTO.getCategory()) {
            // 학습 종류 선택 시
            case "material":
                Material materialClass = Material.builder()
                        .materialCode(masterDataDTO.getCode())
                        .material(masterDataDTO.getName())
                        .materialNote(masterDataDTO.getNote())
                        .build();
                materialRepository.save(materialClass);
                System.out.println("학습 종류 추가 완료");
                break;
            // 학업 구분 선택 시
            case "studyway":
                Studyway studywayClass = Studyway.builder()
                        .studywayCode(masterDataDTO.getCode())
                        .studyway(masterDataDTO.getName())
                        .studywayNote(masterDataDTO.getNote())
                        .build();
                studywayRepository.save(studywayClass);
                System.out.println("학업 구분 추가 완료");
                break;
            // 과목 선택 시
            case "subject":
                Subject subjectClass = Subject.builder()
                        .subjectCode(masterDataDTO.getCode())
                        .subject(masterDataDTO.getName())
                        .subjectNote(masterDataDTO.getNote())
                        .build();
                subjectRepository.save(subjectClass);
                System.out.println("과목 추가 완료");
                break;

            default: // IllegalArgumentException (잘못 된 매개변수가 전달되었을 때 예외)
                throw new IllegalArgumentException("잘못된 카테고리입니다 : " + masterDataDTO.getCategory());
        }
    }

    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }
}
