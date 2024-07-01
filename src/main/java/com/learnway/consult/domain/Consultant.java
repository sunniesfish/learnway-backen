package com.learnway.consult.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Consultant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String consultantId;
    private String password;
    private String name;
    private String subject;
    private String description;
    private String imageUrl;
    private String role;



    public Consultant() {
    }

    public Consultant(Long id, String name, String subject, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}