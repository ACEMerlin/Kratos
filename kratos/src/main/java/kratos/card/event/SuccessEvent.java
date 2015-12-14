package kratos.card.event;

public class SuccessEvent {

    public String code;
    public String message;

    public SuccessEvent(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
