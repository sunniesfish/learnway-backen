package com.example.demo.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.service.ConsultantService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/sse")
public class SseController {

    private Map<Long, SseEmitter> loggedInEmitters = new ConcurrentHashMap<>();
    private Map<Long, Queue<String>> notificationQueue = new ConcurrentHashMap<>();

    @Autowired
    private ConsultantService consultantService;

    @GetMapping("/subscribe/{consultantId}")
    public SseEmitter subscribe(@PathVariable("consultantId") Long consultantId, HttpSession session) {
    	System.out.println("1번 : " + consultantId );
    	System.out.println("1번 : " + (Long)session.getAttribute("loggedInConsultantId"));
        if (session.getAttribute("loggedInConsultantId") == null || !session.getAttribute("loggedInConsultantId").equals(consultantId)) {
            // 상담사가 로그인하지 않은 상태이거나, 다른 상담사의 ID로 요청이 온 경우 처리
        	System.out.println("상담사가 로그인하지 않은 상태이거나, 다른 상담사의 ID로 요청이 온 경우 처리");
            return null;
        }

        System.out.println("subscribe endpoint called for consultant ID: " + consultantId);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        loggedInEmitters.put(consultantId, emitter);

        emitter.onCompletion(() -> {
            System.out.println("Emitter completed for consultant ID: " + consultantId);
            loggedInEmitters.remove(consultantId);
        });
        emitter.onTimeout(() -> {
            System.out.println("Emitter timed out for consultant ID: " + consultantId);
            loggedInEmitters.remove(consultantId);
        });

        // 로그인 시 기존 대기중인 알림들 처리
        Queue<String> notifications = notificationQueue.get(consultantId);
        if (notifications != null) {
            while (!notifications.isEmpty()) {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(notifications.poll()));
                } catch (IOException e) {
                    System.err.println("Error sending notification: " + e.getMessage());
                }
            }
            notificationQueue.remove(consultantId);
        }

        return emitter;
    }

    public void sendNotificationToConsultant(Long consultantId, String message) {
    	System.out.println("2번");
        if (loggedInEmitters.containsKey(consultantId)) {
        	System.out.println("2-1번");
            SseEmitter emitter = loggedInEmitters.get(consultantId);
            try {
                System.out.println("Sending notification to consultant: " + consultantId);
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                System.err.println("Error sending notification: " + e.getMessage());
                loggedInEmitters.remove(consultantId);
            }
        } else {
        	System.out.println("2-2번");
            // 상담사가 로그아웃 상태일 때 대기열에 저장
            Queue<String> notifications = notificationQueue.getOrDefault(consultantId, new ConcurrentLinkedQueue<>());
            notifications.offer(message);
            notificationQueue.put(consultantId, notifications);
        }
    }
}
