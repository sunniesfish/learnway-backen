package com.learnway.schedule.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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

    @Value("${gpt.api.key}")
    private String openaiAccessKey;
//  private String openaiAccessKey = "sk-proj-nKXS22lZfB2oYONgnylxT3BlbkFJHihWNvDP9w8D7w2gmvBO";
	
	
	@GetMapping("/weeklySummary")
    public String weeklySummary(Long memberId, LocalDateTime startOfWeekDateTime, LocalDateTime endOfWeekDateTime) {
		

    	String openaiAccessKey = "000";
        OpenAiService service = new OpenAiService(openaiAccessKey, Duration.ofSeconds(30));
        
        List<Schedule> weeklySchedules = scheduleRepository.findByMemberIdAndStartTimeBetween(
                memberId, 
                startOfWeekDateTime, 
                endOfWeekDateTime
            );
        
        String structuredScheduleData = convertSchedulesToString(weeklySchedules);
        
        List<ChatMessage> messages = new ArrayList<>();
        
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent("당신은 학습 조언 전문가입니다. 주어진 달성율은 학생이 직접 입력한 달성율입니다. 이를 바탕으로 주간 어떤 과목이 더 학습이 필요하고 앞으로의 학습 방향을 500자 이내로 조언해주세요.");
        messages.add(systemMessage); 

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent("다음은 학생의 주간 학습 일정입니다. 이를 바탕으로 학생에게 도움이 될 만한 조언을 500자 이내로 이모티콘을 2~3개만 써서 친근하게 해주세요. 주간일정이 없다면 없다고 말하시오. 지어내지 마시오.:\n\n" + structuredScheduleData);
        messages.add(userMessage);

        // 요청
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(messages)
            .build();

        // 응답 결과
        String result = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        
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
