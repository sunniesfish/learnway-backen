package com.learnway.schedule.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learnway.schedule.domain.Progress;
import com.learnway.schedule.domain.Schedule;
import com.learnway.schedule.domain.ScheduleRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

@Service
public class ApiService {
	
	@Autowired
    private ScheduleRepository scheduleRepository;
	
	
	@GetMapping("/weeklySummary")
    public String weeklySummary(String memberId, LocalDate currentDate) {
		
		LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
    	
    	String openaiAccessKey = "sk-proj-nKXS22lZfB2oYONgnylxT3BlbkFJHihWNvDP9w8D7w2gmvBO";
        OpenAiService service = new OpenAiService(openaiAccessKey, Duration.ofSeconds(30));
        
        List<Schedule> weeklySchedules = scheduleRepository.findByMemberIdAndStartTimeBetween(
                memberId, 
                startOfWeek.atStartOfDay(), 
                endOfWeek.atTime(23, 59, 59)
            );        		
        
        String structuredScheduleData = convertSchedulesToString(weeklySchedules);
        
        List<ChatMessage> messages = new ArrayList<>();
        
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent("당신은 학습 조언 전문가입니다. 주어진 주간 일정을 분석하고, 학생에게 도움이 되는 짧은 조언을 제공해야 합니다. 응답은 100자 이내로 제한합니다.");
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent("다음은 학생의 주간 학습 일정입니다. 이를 바탕으로 학생에게 도움이 될 만한 조언을 해주세요:\n\n" + structuredScheduleData);
        messages.add(userMessage);

        // 요청
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(messages)
            .build();

        // 응답 결과
        String result = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        System.out.println("결과: " + result);
        
        return result;
    }

    private String convertSchedulesToString(List<Schedule> schedules) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Schedule schedule : schedules) {
            sb.append("일정: ").append(schedule.getStartTime().format(formatter))
              .append(" ~ ").append(schedule.getEndTime().format(formatter)).append("\n");
            sb.append("과목: ").append(schedule.getSubjectId().getSubject()).append("\n");
            sb.append("학습 방식: ").append(schedule.getStudywayId().getStudyway()).append("\n");
            sb.append("달성률: ").append(schedule.getScheduleAchieveRate()).append("%\n");
            
            for (Progress progress : schedule.getProgresses()) {
                sb.append("- ").append(progress.getMaterialId().getMaterial())
                  .append(": ").append(progress.getProgress()).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

	
}
