package an.inhaintegration.rentalee.exception;

public class StudentNotAuthorizedException extends RuntimeException {
    public StudentNotAuthorizedException() {
        super("권한이 없습니다.");
    }
}
