package kratos.card.event;


import kratos.card.entity.KData;

public class KOnClickEvent<T extends KData> {

    public T data;
    public String id;
    public String url;
    public int position;

    public KOnClickEvent(String id, T data, String url) {
        this.id = id;
        this.data = data;
        this.url = url;
    }

    public KOnClickEvent(String id, T data, String url, int position) {
        this.id = id;
        this.data = data;
        this.url = url;
        this.position = position;
    }

}
