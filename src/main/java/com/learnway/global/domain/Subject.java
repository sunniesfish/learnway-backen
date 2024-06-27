package com.learnway.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Main 기준 정보 엔티티 클래스 / 과목 : ex.국어, 수학, 영어...
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @Column(length = 4)
    String subjectCode;     // 과목코드 ( 4자리 , PK )

    @Column(nullable = false)
    String subject;         // 과목명 (Not null)
    String subjectNote;     // 과목 비고
}
