package eu.ezlife.ezchat.ezchat.data;

import org.jxmpp.jid.Jid;

/**
 * Created by ajo on 05.02.2017.
 * represents plain old java object from Database Message object
 */
public class ChatHistoryEntry {

    private long id;
    private Jid from;
    private Jid to;
    private String date;
    private String body;
    private long contactId;

    public ChatHistoryEntry(long id, Jid from, Jid to, String date, String body, long contactId){
        this.id = id;
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
        this.contactId = contactId;
    }

    /**
     * Setter + Getter
     */
    public Jid getFrom() {
        return from;
    }

    public Jid getTo() {
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
