package com.learnway.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TargetUni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniId; // 목표 대학 식별키

    @Column(nullable = false)
    private String uniName; // 목표 대학(과) 이름

    private String uniRank; // 지망 순위 : 1순위 / 2순위 / 3순위

    @ManyToOne // 다 대 1 / FK
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}