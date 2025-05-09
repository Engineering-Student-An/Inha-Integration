package an.inhaintegration.icross.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("할 일을 찾을 수 없습니다.");
    }
}
