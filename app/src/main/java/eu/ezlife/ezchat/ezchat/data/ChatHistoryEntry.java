package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 05.02.2017.
 */

/*
* Repesents POJO from DB-Message
* */
public class ChatHistoryEntry {

    private long id;
    private String from;
    private String to;
    private String date;
    private String body;
    private long contactId;

    public ChatHistoryEntry(long id, String from, String to, String date, String body, long contactId){
        this.id = id;
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
        this.contactId = contactId;
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

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    @Override
    public String toString() {
        return getDate() + "\n" + getFrom() + ": " + getBody();
    }

}
