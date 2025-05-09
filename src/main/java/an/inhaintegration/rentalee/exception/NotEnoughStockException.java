package an.inhaintegration.rentalee.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException() {
        super("물품의 재고가 부족합니다.");
    }
}
