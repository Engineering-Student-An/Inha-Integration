package an.inhaintegration.rentalee.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }
}
