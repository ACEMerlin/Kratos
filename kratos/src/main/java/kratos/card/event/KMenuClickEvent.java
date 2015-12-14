package kratos.card.event;

public class KMenuClickEvent {

    public String data;
    public int id;
    public String url;
    public int position;

    public KMenuClickEvent(int id, String data, String url) {
        this.id = id;
        this.data = data;
        this.url = url;
    }

    public KMenuClickEvent(int id, String data, String url, int position) {
        this.id = id;
        this.data = data;
        this.url = url;
        this.position = position;
    }

}
