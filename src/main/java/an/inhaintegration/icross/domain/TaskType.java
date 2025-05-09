package an.inhaintegration.icross.domain;

import lombok.Getter;

@Getter
public enum TaskType {

    // 연체, 진행중, 반납완료
    VIDEO("웹강"),
    ASSIGNMENT("과제"),
    QUIZ("퀴즈");

    private String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }
}
