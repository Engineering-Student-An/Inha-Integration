package an.inhaintegration.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException() {
        super("물품 요청 게시글을 찾을 수 없습니다.");
    }
}
