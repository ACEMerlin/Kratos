package kratos.card.event;

public class ExceptionEvent {
    public String message;
    public String code;

    public ExceptionEvent(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
