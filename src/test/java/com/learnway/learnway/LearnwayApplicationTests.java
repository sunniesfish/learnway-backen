package com.learnway.learnway;

import com.learnway.schedule.domain.Schedule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class LearnwayApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testFindSchedulesByMemberIdAndStartTimeBetween() {
		LocalDateTime start = LocalDateTime.now().minusDays(7);
		LocalDateTime end = LocalDateTime.now();

		List<Schedule> schedules = scheduleRepository.findByMemberIdAndStartTimeBetween(1L, start, end);

		assertNotNull(schedules);
		assertFalse(schedules.isEmpty());
	}

	@Test
	public void testFindFirstScheduleByMemberIdAndStartTimeBetween() {
		LocalDateTime start = LocalDateTime.now().minusDays(7);
		LocalDateTime end = LocalDateTime.now();

		Schedule schedule = scheduleRepository.findFirstByMemberIdAndStartTimeBetween(1L, start, end);

		assertNotNull(schedule);
	}


}
