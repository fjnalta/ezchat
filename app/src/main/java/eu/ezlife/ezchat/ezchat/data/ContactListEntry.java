package eu.ezlife.ezchat.ezchat.data;

import java.io.Serializable;

/**
 * Created by ajo on 04.02.2017.
 * Represents a more complex ContactListEntry which extends the functionality of the
 * Contact DB-Entry Object
 */
public class ContactListEntry extends ContactEntry implements Serializable {

    // Basic Attributes
    private int status;
    private boolean isOnline;
    private boolean isTyping;
    private String lastMessage;

    public ContactListEntry(ContactEntry entry, int status, boolean isOnline, boolean isTyping){
        super(entry.getId(),entry.getUsername(),entry.getAvatar(),entry.getContactName());
        this.status = status;
        this.isTyping = isTyping;
        this.isOnline = isOnline;
        this.lastMessage = "";
    }

    /**
     * Setter + Getter
     */
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}
