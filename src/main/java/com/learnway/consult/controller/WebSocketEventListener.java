//package com.example.demo.controller;
//
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.Set;
//
//@Component
//public class WebSocketEventListener {
//
//    private final SignalingController signalingController;
//
//    public WebSocketEventListener(SignalingController signalingController) {
//        this.signalingController = signalingController;
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        // 세션 ID 가져오기
//        String sessionId = headerAccessor.getSessionId();
// 
//        // 방에서 해당 세션 ID를 가진 참여자 제거
//        signalingController.removeParticipant(sessionId);
//    }
//}

