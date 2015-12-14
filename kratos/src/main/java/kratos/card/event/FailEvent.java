package kratos.card.event;

public class FailEvent {

    public String code;
    public String message;

    public FailEvent(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
