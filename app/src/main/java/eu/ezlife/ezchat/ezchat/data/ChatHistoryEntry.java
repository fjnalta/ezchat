package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 05.02.2017.
 */

public class ChatHistoryEntry {

    private String from;
    private String body;

    public ChatHistoryEntry(String from, String body){
        this.from = from;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return getFrom() + ": " + getBody();
    }

}
