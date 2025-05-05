package an.inhaintegration.exception;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException() {
        super("대여를 찾을 수 없습니다.");
    }
}
