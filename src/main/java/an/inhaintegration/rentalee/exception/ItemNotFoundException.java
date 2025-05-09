package an.inhaintegration.rentalee.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
        super("물품을 찾을 수 없습니다.");
    }
}
