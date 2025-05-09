package an.inhaintegration.rentalee.service;

import an.inhaintegration.rentalee.domain.Rental;
import an.inhaintegration.rentalee.domain.RentalStatus;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlarmService {

    private final RentalRepository rentalRepository;
    private final EmailService emailService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")    // 매일 0시에 실행하게끔 스케쥴링
    public void alarmDueDate() {

        // 대여 진행 중인 목록 조회
        List<Rental> rentalsByStatus = rentalRepository.findRentalsByStatus(RentalStatus.ING);

        for (Rental rental : rentalsByStatus) {

            // 대여 일 계산
            long between = ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDate.now());

            // D-1 이거나 D-Day인 경우
            if(between == 2 || between == 3) {
                Student student = rental.getStudent();
                String email = student.getEmail();
                String name = student.getName();
                String itemName = rental.getItem().getName();
                emailService.sendAlarm(email, name, itemName, (between == 2) ? "email/alarmD1" : "email/alarmDDay");
            }
        }
    }
}
