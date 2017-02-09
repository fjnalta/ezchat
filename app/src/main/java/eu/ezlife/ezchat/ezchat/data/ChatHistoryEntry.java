package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 05.02.2017.
 */

public class ChatHistoryEntry {

    private long id;
    private String from;
    private String to;
    private String date;
    private String body;
    private int contactsId;

    public ChatHistoryEntry(long id, String from, String to, String date, String body, int contactsId){
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
        this.id = id;
        this.contactsId = contactsId;
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

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactsId() {
        return contactsId;
    }

    public void setContactsId(int contactsId) {
        this.contactsId = contactsId;
    }

    @Override
    public String toString() {
        return getDate() + " - " + getFrom() + ": " + getBody();
    }

}
