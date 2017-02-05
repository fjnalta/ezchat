package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 05.02.2017.
 */

public class ChatHistoryEntry {

    private String date;
    private String from;
    private String to;
    private String body;

    public ChatHistoryEntry(String from, String to, String body, String date){
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public String getDate(){
        return date;
    }

    @Override
    public String toString() {
        return getDate() + " - " + getFrom() + ": " + getBody();
    }

}
