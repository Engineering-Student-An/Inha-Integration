package an.inhaintegration.icross.exception;

public class UnivInfoNotFoundException extends RuntimeException {
    public UnivInfoNotFoundException() {
        super("학교 관련 정보를 찾을 수 없습니다.");
    }
}
