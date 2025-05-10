package an.inhaintegration.icross.service;

import an.inhaintegration.icross.domain.Schedule;
import an.inhaintegration.icross.domain.Subject;
import an.inhaintegration.icross.domain.UnivInfo;
import an.inhaintegration.icross.exception.SubjectNotFoundException;
import an.inhaintegration.icross.exception.UnivInfoNotFoundException;
import an.inhaintegration.icross.repository.ScheduleRepository;
import an.inhaintegration.icross.repository.SubjectRepository;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import an.inhaintegration.icross.repository.UnivInfoTaskRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UnivInfoRepository univInfoRepository;
    private final SubjectRepository subjectRepository;
    private final UnivInfoTaskRepository univInfoTaskRepository;
    private final OpenAIService openAIService;

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Transactional
    public void save(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

//    @Transactional
//    public void saveByForm(Long studentId, ScheduleForm scheduleForm) {
//
//        String time = scheduleForm.getStartTime() + "~" + scheduleForm.getEndTime();
//        Schedule schedule = Schedule.builder()
//                        .studentId(studentId)
//                        .name(scheduleForm.getScheduleName())
//                        .time(time)
//                        .completed(false).build();
//
//        scheduleRepository.save(schedule);
//    }

    @Transactional
    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    public List<Schedule> findByStudentId(Long studentId) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        return scheduleRepository.findSchedulesByUnivInfoIdOrderByTime(univInfo.getId());
    }

    @Transactional
    public void createSchedule(Long studentId) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        // 수강중인 과목 이름 및 설명 추가
        List<String> subjectName = univInfo.getSubjectList().stream()
                .map(subjectId -> { Subject subject = subjectRepository.findById(subjectId).orElseThrow(SubjectNotFoundException::new);
                    return "과목 이름 : " + subject.getName() + ", 과목 시간 : " + subject.getTimeList();
                })
                .toList();

        // 완료되지 않은 할 일 설명 추가
        List<String> taskName = univInfoTaskRepository.findUnivInfoTasksByCompletedIs(false).stream()
                .map(uit -> "해당 할 일의 과목 이름 : " + uit.getTask().getSubject().getName() +
                        ", 할 일 이름 : " + uit.getTask().getName() +
                        ", 마감일 : " + uit.getTask().getDeadline())
                .toList();

        String prompt = "나의 강의 리스트는 다음과 같아" + subjectName +
                "그리고 나에게 남은 할 일은 각각 다음과 같아." + taskName +
                "오늘 요일은 " + LocalDate.now().getDayOfWeek() + "이야. 꼭 스케줄에 오늘 요일도 고려해서 넣어줘 " +
                "나에게 오늘 하루 추천 스케줄을 알려줘, 이때 복습, 휴식, 점심시간, 저녁시간도 고려해서 넣어줘 반드시 오전 9시부터 24시까지의 스케줄을 알려줘야만 해" +
                "그리고 오늘 진행하는 강의 이외의 강의는 절대 오늘 시간표에 넣지마. 예를 들어 오늘 수업 없음 이라면 시간표에 강의를 아무것도 넣지 말라는 소리야." +
                "강의 시간도 정확하게 고려해서 작성해줘. 10:30-12:00 수업인데 12:00-13:30 수업으로 잘못 쓰지 말라는 이야기야.";

        String result = openAIService.schedule(prompt);

        // jsonString을 JSONObject로 변환
        JSONObject obj = new JSONObject(result);

        // "choices" 배열에서 첫 번째 요소를 가져옴
        JSONObject firstChoice = obj.getJSONArray("choices").getJSONObject(0);

        // "message" 객체에서 "content" 값을 추출
        String content = firstChoice.getJSONObject("message").getString("content");

        // 리스트화 해서 스케줄에 저장
        List<String> times = new ArrayList<>();
        List<String> contents = new ArrayList<>();

        String[] scheduleParts = content.split(", ");

        for (String part : scheduleParts) {
            // 각 부분을 "/” 기준으로 시간과 내용으로 분리
            String[] splitPart = part.split("/", 2);
            times.add(splitPart[0]); // 시간을 리스트에 추가
            contents.add(splitPart[1]); // 내용을 리스트에 추가
        }

        for (int i = 0; i < times.size(); i++) {

            // 시간 앞에 0 추가 (ex. 9:00 -> 09:00)

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String[] split = times.get(i).split("-");
            LocalTime startTime = LocalTime.parse(split[0], inputFormatter);
            LocalTime endTime = LocalTime.parse(split[1], inputFormatter);

            String formattedTime = outputFormatter.format(startTime) + "~" + outputFormatter.format(endTime);

            Schedule schedule = Schedule.builder()
                    .content(contents.get(i))
                    .time(formattedTime)
                    .completed(false)
                    .build();
            schedule.setUnivInfo(univInfo);

            scheduleRepository.save(schedule);
        }
    }

//    public Schedule findById(Long scheduleId) {
//        return scheduleRepository.findScheduleById(scheduleId);
//    }
//
//    @Transactional
//    public void setCompleted(Long scheduleId) {
//        scheduleRepository.findScheduleById(scheduleId).setCompleted();
//    }


}
