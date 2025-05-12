package an.inhaintegration.icross.service;

import an.inhaintegration.icross.domain.Subject;
import an.inhaintegration.icross.domain.Task;
import an.inhaintegration.icross.domain.TaskType;
import an.inhaintegration.icross.domain.UnivInfo;
import an.inhaintegration.icross.dto.CourseResponseDto;
import an.inhaintegration.icross.dto.CoursemosResponseDto;
import an.inhaintegration.icross.dto.TaskRequestDto;
import an.inhaintegration.icross.exception.CoursemosCrawlingException;
import an.inhaintegration.icross.exception.SubjectNotFoundException;
import an.inhaintegration.icross.exception.UnivInfoNotFoundException;
import an.inhaintegration.icross.repository.SubjectRepository;
import an.inhaintegration.icross.repository.TaskRepository;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CoursemosService {

    private final RestTemplate restTemplate;
    private final SubjectService subjectService;
    private final TaskRepository taskRepository;
    private final SubjectRepository subjectRepository;
    private final UnivInfoRepository univInfoRepository;

    public String getWstoken() {

        String url = "https://api2.naddle.kr/api/v1/cos_com_school?keyword=" + "인하대학교" + "&lang=ko";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "https://api2.naddle.kr");
        headers.setConnection("keep-alive");
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
        headers.add("Accept-Language", "ko-US;q=1.0, en-US;q=0.9");
        headers.add("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJcYkNvdXJzZW1vcyIsImlhdCI6MTY5NzUyMzU1NiwiZXhwIjoyMDQ0NTkyNjI1LCJhdWQiOiJuYWRkbGUua3IiLCJzdWIiOiJyYWJiaXRAZGFsYml0c29mdC5jb20iLCJ1c2VyX2lkIjoiMSJ9.-799jl5c466FLKWoKld1PuOzfDb6FUHjauT-_XNVj0k");

        HttpEntity<String> request = new HttpEntity<>(headers);

        // 프록시 세팅
//        System.setProperty("http.proxyHost", "localhost");
//        System.setProperty("http.proxyPort", "9494");

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        // 프록시 세팅 초기화
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");

        // ws토큰 추출을 위한 JSON 응답 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");
            if (dataNode.isArray() && !dataNode.isEmpty()) {
                JsonNode firstItem = dataNode.get(0);
                return firstItem.path("wstoken").asText();
            }
            throw new CoursemosCrawlingException();
        } catch (Exception e) {
            throw new CoursemosCrawlingException();
        }
    }

    public String login(String userId, String password, String wsToken) {

        String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setConnection("keep-alive");
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add("User-Agent", "coursemos_swift/2.2.2 (kr.coursemos.ios2; build:9663; iOS 17.4.1) Alamofire/4.9.1");
        headers.add("Accept-Language", "ko-KR;q=1.0, en-KR;q=0.9");

        String body = "lang=ko&moodlewsrestformat=json&password=" + password + "&userid=" + userId + "&wsfunction=coursemos_user_login_v2&wstoken=" + wsToken;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        try {
            String jsonString = response.getBody();
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열을 JsonNode 객체로 변환
            JsonNode rootNode = objectMapper.readTree(jsonString);
            // "data" 객체로 이동
            JsonNode dataNode = rootNode.path("data");
            // "utoken" 값 추출
            String utoken = dataNode.get("utoken").textValue();
            return utoken;
        } catch (Exception e) {
            throw new CoursemosCrawlingException();
        }
    }

    // 수강중인 과목의 id를 가져오는 메서드
    public List<Long> getCourseIds(String utoken) {

        String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1715901782.18.0.1715901782.0.0.0; _ga=GA1.1.33679238.1714311933");
        headers.set("Connection", "keep-alive");
        headers.set("Accept", "*/*");
        headers.set("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
        headers.setAcceptLanguageAsLocales(Collections.singletonList(Locale.forLanguageTag("ko-KR")));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("lang", "ko");
        map.add("moodlewsrestformat", "json");
        map.add("wsfunction", "coursemos_course_get_mycourses_v2");
        map.add("wstoken", utoken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String jsonString = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        List<Long> ids = new ArrayList<>();
        try {
            CoursemosResponseDto coursemosResponseDto = mapper.readValue(jsonString, CoursemosResponseDto.class);
            for (CourseResponseDto course : coursemosResponseDto.getData()) {
                if (course.getCu_visible() == 1 && !course.getDay_cd().isEmpty()) {
                    ids.add(course.getId());

                    // 존재하지 않는 과목은 저장
                    if(!subjectRepository.existsById(course.getId())) {
                        subjectService.save(course.getId(), course.getIdnumber(), course.getFullname(), course.getDay_cd(), course.getHour1());
                    }
                }
            }

        } catch (Exception e) {
            throw new CoursemosCrawlingException();
        }

        // 학생의 수강 전체 리스트 (courseId 리스트) 반환
        return ids;
    }

    // 특정 강의의 할 일 리스트 가져오기
    public List<TaskRequestDto> getTaskList(String utoken, Long courseId) {

        try {
            // ========================== 각 과목 페이지 입장 ============================
            // URL 설정
            String url = "https://learn.inha.ac.kr/webservice/rest/server.php?courseid=" + courseId +
                    "&lang=ko&moodlewsrestformat=json&wsfunction=coursemos_course_get_contents_v2&wstoken=" + utoken;

// 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Host", "learn.inha.ac.kr");
            headers.set("Connection", "keep-alive");
            headers.set("Accept", "*/*");
            headers.set("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
            headers.set("Accept-Language", "ko-KR;q=1.0, io-KR;q=0.9");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1716901219.1.1.1716901326.0.0.0; _ga=GA1.1.833759194.1716901220; MoodleSession=8s9fovhei8rtj145u1rb82ma3m; ubboard_read=%25AA%25A5ej%25C8%2593%25F6%25BEa%250B%2500i%25BA%2596");

// HttpEntity 생성 (GET 요청의 경우 바디는 필요 없음)
            HttpEntity<String> entity = new HttpEntity<>(headers);

// RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();

// 요청 실행
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);


            // ========================== 각 페이지에서 동영상, 과제 찾음 =========================
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // JSON 문자열을 JsonNode로 파싱
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataArray = rootNode.get("data");

                // 결과를 저장할 리스트
                List<TaskRequestDto> taskRequestDtos = new ArrayList<>();

                // 모든 요소를 순회하며 조건에 맞는 id 추출
                if (dataArray.isArray()) {
                    for (JsonNode section : dataArray) {
                        JsonNode modulesArray = section.get("modules");
                        if (modulesArray != null && modulesArray.isArray()) {
                            for (JsonNode module : modulesArray) {
                                String modname = module.get("modname").asText();

                                Long taskId = module.get("id").asLong();    // 추출한 webId
                                String name = module.get("name").asText();  // 할 일의 이름

                                if (!taskRepository.existsById(taskId)) {
                                    switch (modname) {
                                        case "vod" -> taskRequestDtos.add(new TaskRequestDto(taskId, courseId, name, TaskType.VIDEO));
                                        case "assign" -> taskRequestDtos.add(new TaskRequestDto(taskId, courseId, name, TaskType.ASSIGNMENT));
                                        case "quiz" -> taskRequestDtos.add(new TaskRequestDto(taskId, courseId, name, TaskType.QUIZ));
                                    }
                                }
                            }
                        }
                    }
                }
                return taskRequestDtos;

            } catch (IOException e) {
                throw new CoursemosCrawlingException();
            }
        } catch (Exception e) {
            throw new CoursemosCrawlingException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveVideo(String utoken, TaskRequestDto taskRequestDto) {

        Long videoId = taskRequestDto.getWebId();

        // 헤더 설정
        HttpHeaders headers = createHttpHeaders();

        headers.add("Cookie", "_ga_E323M45YWM=GS2.1.s1746807549$o2$g1$t1746807560$j0$l0$h0; moodle_notice_1_275435=hide; moodle_notice_1_689387=hide; moodle_notice_1_768120=hide; moodle_notice_1_777569=hide; MoodleSession=id40o9t44m63ide3585c5quo60; _ga=GA1.1.1990892485.1746804782");

        // 바디 데이터 설정
        String body = "utoken="+utoken+"&modurl=https%3A//learn.inha.ac.kr/mod/vod/view.php?id%3D" + videoId;

        // HttpEntity에 헤더와 데이터 설정
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // RestTemplate에 HttpClient 사용 설정
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // 첫 번째 요청을 실행하고, 자동으로 리다이렉트됨
        String response = restTemplate.exchange(
                "https://learn.inha.ac.kr/mod/vod/view.php?id=" + videoId,
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();

        Document doc = (Document) Jsoup.parse(response);

        // "출석인정기간"에 해당하는 span 요소를 찾습니다.
        Elements vodInfoElements = doc.select("div.vod_info");
        for (Element element : vodInfoElements) {
            Elements infoLabels = element.getElementsByClass("vod_info");
            for (Element label : infoLabels) {
                if (label.text().contains("출석인정기간:")) {
                    Element valueElement = element.selectFirst(".vod_info_value");
                    if (valueElement != null) {
                        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

                        // 문자열을 LocalDateTime 객체로 변환
                        LocalDateTime dateTime = LocalDateTime.parse(valueElement.text(), formatter);

                        // 아직 존재하지 않을때 Task 저장
                        if(!taskRepository.existsById(videoId)) saveTask(taskRequestDto, dateTime, TaskType.VIDEO);
                    }
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAssign(String utoken, TaskRequestDto taskRequestDto) {

        Long assignId = taskRequestDto.getWebId();
        // 헤더 설정
        HttpHeaders headers = createHttpHeaders();
//        headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");
        headers.set("Cookie", "_ga_E323M45YWM=GS2.1.s1746804781$o1$g1$t1746804808$j0$l0$h0; MoodleSession=id40o9t44m63ide3585c5quo60; _ga=GA1.1.1990892485.1746804782");

        // 바디 데이터 설정
        String body = "utoken="+utoken+"&modurl=https%3A//learn.inha.ac.kr/mod/assign/view.php?id%3D" + assignId;

        // HttpEntity에 헤더와 데이터 설정
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // RestTemplate에 HttpClient 사용 설정
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        String response = restTemplate.exchange(
                "https://learn.inha.ac.kr/mod/assign/view.php?id=" + assignId,
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();

        Document doc = (Document) Jsoup.parse(response);

        // "종료 일시"가 포함된 행을 선택
        Element endDateRow = doc.select("td:contains(종료 일시)").first();

        if(endDateRow != null) {
            Element endDate = endDateRow.nextElementSibling();
            // DateTimeFormatter 정의 (문자열 형태에 맞춤)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // 문자열을 LocalDateTime 객체로 변환
            LocalDateTime dateTime = LocalDateTime.parse(endDate.text(), formatter);
            // 아직 존재하지 않을때 Task 저장
            if(!taskRepository.existsById(assignId)) saveTask(taskRequestDto, dateTime, TaskType.ASSIGNMENT);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveQuiz(String utoken, TaskRequestDto taskRequestDto) {

        Long quizId = taskRequestDto.getWebId();
        // 헤더 설정
        HttpHeaders headers = createHttpHeaders();
        headers.set("Cookie", "_ga_E323M45YWM=GS2.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");

        // 바디 데이터 설정
        String body = "utoken="+utoken+"&modurl=https%3A//learn.inha.ac.kr/mod/quiz/view.php?id%3D" + quizId;

        // HttpEntity에 헤더와 데이터 설정
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // RestTemplate에 HttpClient 사용 설정
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // 첫 번째 요청을 실행하고, 자동으로 리다이렉트됨
        String response = restTemplate.exchange("https://learn.inha.ac.kr/local/coursemos/webviewapi.php?lang=ko",
                HttpMethod.GET,
                entity, // 첫 번째 요청의 HttpEntity
                String.class).getBody();

        Document doc = (Document) Jsoup.parse(response);

        // "종료 일시"가 포함된 행을 선택
        Element endDate = doc.select("p:contains(종료일시)").getFirst();

        String[] date = endDate.text().split(" ");
        String endDateString = date[2] + " " + date[3];

        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 문자열을 LocalDateTime 객체로 변환
        LocalDateTime dateTime = LocalDateTime.parse(endDateString, formatter);

        // 마감기한이 오늘보다 뒤에 있거나 아직 존재하지 않을때 AllVideoLecture 저장
        if(!taskRepository.existsById(quizId)) saveTask(taskRequestDto, dateTime, TaskType.QUIZ);
    }

    // coursemos 로그인 이후 크롤링 및 없는 엔티티 생성하는 메서드
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fromLoginToSaveUnivInfoTask(String stuId, Long studentId) {

        // wstoken 가져오기
        String wstoken = getWstoken();
        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);
        String password = univInfo.getPassword();

        // utoken 가져오기
        String utoken = login(stuId, password, wstoken);
        System.out.println("utoken = " + utoken);
        // 수강중인 강의의 id 가져오기
        List<Long> courseIds = getCourseIds(utoken);
        List<Long> existingSubjects = univInfo.getSubjectList();
        for (Long courseId : courseIds) {
            if (!existingSubjects.contains(courseId)) {
                existingSubjects.add(courseId);
            }
        }

        List<Long> taskIds = new ArrayList<>();
        try {
            for (Long courseId : courseIds) {
                System.out.println("courseId = " + courseId);
                List<TaskRequestDto> taskList = getTaskList(utoken, courseId);
                System.out.println("taskList.size() = " + taskList.size());
                for (TaskRequestDto taskRequestDto : taskList) {
                    // 기존에 없던 Task 라면 저장
                    taskIds.add(taskRequestDto.getWebId());
                    if (!taskRepository.existsById(taskRequestDto.getWebId())) {
                        switch (taskRequestDto.getTaskType()) {
                            case VIDEO -> saveVideo(utoken, taskRequestDto);
                            case ASSIGNMENT -> saveAssign(utoken, taskRequestDto);
                            case QUIZ -> saveQuiz(utoken, taskRequestDto);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("ex.getMessage() = " + ex.getMessage());
            System.out.println("ex.getLocalizedMessage() = " + ex.getLocalizedMessage());
        }
        System.out.println("taskIds.size() = " + taskIds.size());

    }

    private HttpHeaders createHttpHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.set("Sec-Fetch-Site", "none");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
        headers.set("Sec-Fetch-Mode", "navigate");
        headers.set("Origin", "null");
        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Dest", "document");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return headers;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveTask(TaskRequestDto taskRequestDto, LocalDateTime dateTime, TaskType taskType) {

        Subject subject = subjectRepository.findById(taskRequestDto.getSubjectId())
                .orElseThrow(SubjectNotFoundException::new);

        Optional<Task> existingTask = taskRepository.findById(taskRequestDto.getWebId());

        if (existingTask.isEmpty()) {
            Task task = Task.builder()
                    .id(taskRequestDto.getWebId())
                    .name(taskRequestDto.getName())
                    .deadline(dateTime)
                    .type(taskType)
                    .subject(subject)
                    .build();

            taskRepository.save(task);
        }
    }
}
