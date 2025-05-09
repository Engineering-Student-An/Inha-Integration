package an.inhaintegration.rentalee.exception;

public class RentalOverdueException extends RuntimeException {
    public RentalOverdueException() {
        super("대여를 완료할 수 없습니다.");
    }
}
