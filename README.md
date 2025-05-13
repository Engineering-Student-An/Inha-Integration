# README

---

## 프로젝트 소개

---

### [SWITCH (Inha-Integration)](http://ec2-13-209-198-107.ap-northeast-2.compute.amazonaws.com:8082/)

<p>
  <img src="https://mosoobucket.s3.ap-northeast-2.amazonaws.com/rental-logo.png" width="200"/>
  <img src="https://mosoobucket.s3.ap-northeast-2.amazonaws.com/icross-logo.png" width="200"/>
</p>


SWITCH는 인하대학교 전자공학과 학생회의 대여 사업과 학업 지원 서비스를 제공하는 웹 애플리케이션입니다.

학생회비 납부 인원에 한해 회원가입 및 이용 가능합니다.

- 대여 사업
    - 대여 물품의 현황을 확인하고 대여 신청할 수 있으며, 반납일에 관한 알림이 이메일로 전송됩니다.
    - 학교 홈페이지에 게시된 공지사항을 홈 화면에서 확인할 수 있고, 게시판으로 유저 간 소통이 가능합니다.

- I-CROSS
    - 인하대학교 LMS (Learning Management System, [I-Class](https://learn.inha.ac.kr/)) 의 계정 정보 입력 후 이용 가능합니다.
    - 수강 중인 강의 정보와 남은 웹강, 과제, 퀴즈 등을 크롤링하고 마감 임박 시 알림을 전송합니다.
    - Chat-GPT가 해당 정보들을 토대로 하루의 스케줄을 추천하고 카카오톡으로 나에게 전송 가능합니다.
    - Chat-GPT가 업로드한 강의 자료를 기반으로 예상 시험 문제를 생성합니다.
<br><br>

## 개발 기간

---

2023.10 ~ 진행 중

- 배포 및 업데이트 내역
    - 2024-03 : 배포 시작
    - 2024-08 : v2 리팩토링
    - 2025-05 : v3 리팩토링 (I-CROSS)
<br><br>

## 기술 스택

---

### Backend

- Java 21
- Spring Boot 3.2.2
- Spring MVC, Spring Data JPA, Spring Security, Spring Validation
- QueryDSL
- OAuth2
- Spring WebFlux (WebClient)
- MySQL

### Infra & Tooling

- Gradle
- Lombok

### File & Data Handling

- Apache POI (Excel)
- Apache PDFBox (PDF)
- Jsoup (HTML Parsing)
- Gson, org.json (JSON 처리)
- Apache HttpClient / HttpClient5

### View (Front-end)

- Thymeleaf
- HTML, CSS, JavaScript
- TailwindCSS (유틸리티 기반 스타일링)

### Infrastructure

- AWS EC2: Ubuntu 22.04 LTS 기반 가상 서버 환경, 유연한 서버 관리 및 확장성 제공
- AWS RDS (MySQL 8.0.35): 안정적인 운영을 위한 관리형 관계형 데이터베이스 서비스
- Docker: 애플리케이션 컨테이너화 및 이식성을 위한 환경 구성. Dockerfile을 직접 작성하여 이미지 빌드 및 실행

### Domain & DNS

- AWS Route 53: 도메인 등록 및 DNS 트래픽 라우팅 관리

### CI/CD

- GitHub Actions: 테스트 및 빌드 자동화, 배포 파이프라인 구축을 통한 개발 효율성 향상
- Docker Hub: 빌드된 Docker 이미지를 저장하고, 배포 환경에서 직접 pull하여 사용

### 버전 및 이슈관리

- Git, GitHub

<br><br>
## 주요 기능

---

### 회원

- id, 비밀번호를 직접 입력하거나 Google, Kakao, Naver, Github 계정을 연동한 소셜 로그인 가능합니다.
- 학생회비 납부 명단에 포함된 정보에 한해 학번, 이름 검증 후 이메일 인증 후에 회원가입 가능합니다.

### 대여

- 카테고리 (기타, 전공서적) 선택 후 대여 가능한 물품에 한해 대여 신청 가능합니다.
- 대여 이후 반납 기한 알림을 이메일로 전송합니다.
- 연체 시 연체료가 자동 계산되며 반납 버튼 클릭 시 관리자와의 오픈 채팅으로 연결됩니다.

### 물품

- 카테고리, 이름을 포함한 검색이 가능하며 물품의 현황을 확인 가능합니다.
- 물품 현황 수정 요청 게시판을 통해 현황 수정 건의 가능합니다.

### 게시판

- 인하대학교 홈페이지에서 크롤링한 학부 중요 공지사항, 최근 공지사항를 홈 화면에서 확인 가능합니다.
- 공지사항, 자유 게시판, 건의 게시판 사용 가능하며 댓글, 좋아요 기능 사용 가능합니다.

### 스케줄, 과제

- 입력한 I-Class 계정 정보를 통해 수강 중인 강의 정보와 남은 과제등을 크롤링합니다.
- 강의에 등록된 과제, 웹강, 퀴즈 정보를 저장하며 마감 임박 시 알림을 전송합니다.
- 해당 할 일과 오늘의 강의등을 토대로 Chat-GPT가 추천해주는 오늘의 스케줄을 확인할 수 있습니다.
- 카카오톡으로 스케줄을 나에게 전송할 수 있습니다.

### 예상 시험 문제

- 강의 자료 업로드 후 문제 유형과 문제 개수를 선택합니다.
- Chat-GPT가 생성한 예상 시험 문제와 정답을 확인 가능합니다.

<br><br>
## 상세 기능

---

[기능 명세서](https://github.com/Engineering-Student-An/Inha-Integration/wiki/%EA%B8%B0%EB%8A%A5-%EB%AA%85%EC%84%B8%EC%84%9C)

<br><br>
## ERD

---

![inhaeeDB.png](https://mosoobucket.s3.ap-northeast-2.amazonaws.com/inhaeeDB.png)
<br><br>
## 프로젝트 구조

---

- 프로젝트 구조
    
    ```
    src
    ├── main
    │   └── java
    │       └── an
    │           └── inhaintegration
    │               ├── config
    │               ├── icross
    │               │   ├── api
    │               │   ├── controller
    │               │   ├── domain
    │               │   ├── dto
    │               │   ├── exception
    │               │   ├── repository
    │               │   └── service
    │               ├── oauth2
    │               └── rentalee
    │                   ├── api
    │                   ├── controller
    │                   ├── domain
    │                   ├── dto
    │                   ├── exception
    │                   ├── repository
    │                   └── service
    └── resources
        ├── static
        │   ├── css
        │   ├── images
        │   └── js
        └── templates
            ├── email
            ├── error
            ├── fragments
    		    ├── icross
    		    │   ├─ home
    		    │   ├─ quiz
    		    │   ├─ schedule
    		    │   └─ univinfo
            └── rentalee
    		        ├─ admin
    		        │  ├─ item
    		        │  ├─ rental
    		        │  └─ student
    		        ├─ board
    		        ├─ home
    		        │  └─ join
    		        ├─ item
    		        ├─ proposal
    		        ├─ rental
    		        └─ student
    ```