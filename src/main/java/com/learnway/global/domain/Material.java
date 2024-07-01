package com.learnway.global.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Main 기준 정보 엔티티 클래스 / 학습 종류 : ex. 교과서, 교재, 프린트물...
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @Id
    @Column(length = 4)
    String materialCode;       // 학습 종류 코드 ( 4자리, PK)

    @Column(nullable = false)
    String material;       // 학습 종류명 (Not null)
    String materialNote;         // 학습 종류 비고
    
    // getName() 메서드 추가
    public String getName() {
        return this.material;
    }
}
