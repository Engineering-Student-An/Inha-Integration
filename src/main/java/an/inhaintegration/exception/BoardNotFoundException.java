package an.inhaintegration.exception;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException() {
        super("게시글을 찾을 수 없습니다.");
    }
}
