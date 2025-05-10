package an.inhaintegration.icross.service;

import an.inhaintegration.icross.domain.UnivInfoTask;
import an.inhaintegration.icross.repository.ScheduleRepository;
import an.inhaintegration.icross.repository.UnivInfoTaskRepository;
import an.inhaintegration.rentalee.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class ScheduledTask {

    private final EmailService emailService;
    private final UnivInfoTaskRepository univInfoTaskRepository;
    private final ScheduleRepository scheduleRepository;

    // 할 일 알림 메일 보내기
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void announcement() {

        // 완료되지 않은 마감기한이 3일 이하로 남은 UnivInfoTask 조회
        List<UnivInfoTask> univInfoTaskList = univInfoTaskRepository.findIncompleteTasksDueWithinThreeDays(LocalDateTime.now().plusDays(3));

        for (UnivInfoTask univInfoTask : univInfoTaskList) {

            long dayDifference = ChronoUnit.DAYS.between(LocalDate.now(), univInfoTask.getTask().getDeadline());

            emailService.sendICrossDDays(univInfoTask.getUnivInfo().getStudent().getEmail(),
                    univInfoTask.getUnivInfo().getStudent().getName(),
                    univInfoTask.getTask().getName() + " (" + univInfoTask.getTask().getSubject().getName() + ")",
                    dayDifference > 0 ? "email/iCrossAlarmDDays" : "email/iCrossAlarmToday",
                    (int) dayDifference);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    // 스케줄 초기화
    public void clearSchedule() {

        scheduleRepository.deleteAllInBatch();
    }
}
