package an.inhaintegration.icross.exception;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException() {
        super("과목을 찾을 수 없습니다.");
    }
}
