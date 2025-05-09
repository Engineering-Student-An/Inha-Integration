package an.inhaintegration.icross.exception;

public class CoursemosCrawlingException extends RuntimeException {
    public CoursemosCrawlingException() { super("Coursemos에서 크롤링 과정 중 예외가 발생했습니다."); }
}
