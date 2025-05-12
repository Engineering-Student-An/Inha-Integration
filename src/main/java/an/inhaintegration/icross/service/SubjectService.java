package an.inhaintegration.icross.service;

import an.inhaintegration.icross.domain.Subject;
import an.inhaintegration.icross.domain.UnivInfo;
import an.inhaintegration.icross.dto.SubjectResponseDto;
import an.inhaintegration.icross.exception.SubjectNotFoundException;
import an.inhaintegration.icross.exception.UnivInfoNotFoundException;
import an.inhaintegration.icross.repository.SubjectRepository;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UnivInfoRepository univInfoRepository;

    @Transactional
    public void save(Long subjectId, String code, String name, String day, String hour) {

        String[] splitCode = code.split("_");
        String[] splitDay = day.split("<BR>");
        String[] splitHour = hour.split("<BR>");

        List<String> time = new ArrayList<>();
        for (int i = 0; i < splitDay.length; i++) {
            // 웹강 과목이 아닌 경우
            if(!splitDay[i].equals("WEB")) {
                String[] splitHour2 = splitHour[i].split("~");
                time.add(splitDay[i] + transformTime(Integer.parseInt(splitHour2[0])) + "~" + transformTime(Integer.parseInt(splitHour2[1])+1));
            }

            // 웹강 과목인 경우
            else {
                time.add("WEB");
                break;
            }
        }

        Subject subject = Subject.builder()
                .id(subjectId)
                .name(name)
                .code(splitCode[2] + "-" + splitCode[3])    // 학수번호
                .timeList(time).build();

        subjectRepository.save(subject);
    }

    public LocalTime transformTime(int time) {

        // 9시 시작
        LocalTime baseTime = LocalTime.of(9, 0);

        return baseTime.plusMinutes((time - 1) * 30L);
    }

    public List<SubjectResponseDto> findTodaySubjects(Long studentId) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        String koreanDay = getKoreanDayOfWeek(LocalDate.now().getDayOfWeek());

        return univInfo.getSubjectList().stream()
                .map(subjectId -> subjectRepository.findById(subjectId).orElseThrow(SubjectNotFoundException::new))
                .flatMap(subject -> subject.getTimeList().stream()
                        .filter(time -> time.contains(koreanDay))
                        .map(time -> {
                            String timePart = time.replaceAll("^[가-힣]+", "");
                            return new SubjectResponseDto(subject.getName(), timePart);
                        }))
                .sorted(Comparator.comparing(SubjectResponseDto::getTime))
                .collect(Collectors.toList());
    }

    public List<SubjectResponseDto> findAllSubjects(Long studentId) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        return univInfo.getSubjectList().stream()
                .map(subjectId -> subjectRepository.findById(subjectId)
                        .orElseThrow(SubjectNotFoundException::new))
                .map(subject -> {
                    String time = String.join(", ", subject.getTimeList());
                    return new SubjectResponseDto(subject.getName(), subject.getCode(), time);
                })
                .collect(Collectors.toList());
    }

    private static String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                return "";
        }
    }
}
