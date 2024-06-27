package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReservationRequest {
    private String userId;
    private Long counselor;
    private String reservationContent;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;


}


 