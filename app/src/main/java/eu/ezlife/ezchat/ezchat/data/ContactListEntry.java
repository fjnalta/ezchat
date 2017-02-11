package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 04.02.2017.
 */

public class ContactListEntry {

    private int status;
    private boolean isOnline;
    private boolean isTyping;
    private long contactId;

    public ContactListEntry(int status, boolean isOnline, boolean isTyping, long contactId){
        this.status = status;
        this.isTyping = false;
        this.isOnline = false;
        this.contactId = contactId;
    }

    // Setter + Getter
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    @Override
    public String toString() {
        return getStatus() + "";
    }
}
