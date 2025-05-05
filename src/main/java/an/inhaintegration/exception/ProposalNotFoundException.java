package an.inhaintegration.exception;

public class ProposalNotFoundException extends RuntimeException {

    public ProposalNotFoundException() {
        super("건의 게시글을 찾을 수 없습니다.");
    }
}
