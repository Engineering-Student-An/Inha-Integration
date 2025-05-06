package an.inhaintegration.exception;

public class StudentNotAuthorizedException extends RuntimeException {
    public StudentNotAuthorizedException() {
        super("권한이 없습니다.");
    }
}
