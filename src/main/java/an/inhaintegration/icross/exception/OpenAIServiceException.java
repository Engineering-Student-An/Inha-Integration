package an.inhaintegration.icross.exception;

public class OpenAIServiceException extends RuntimeException {
    public OpenAIServiceException(String errorMessage) { super(errorMessage); }
}
