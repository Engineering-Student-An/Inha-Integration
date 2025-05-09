package an.inhaintegration.rentalee.exception;

public class ReplyNotFoundException extends RuntimeException {
    public ReplyNotFoundException() {
        super("댓글을 찾을 수 없습니다.");
    }
}
