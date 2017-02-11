package eu.ezlife.ezchat.ezchat.data;

import eu.ezlife.ezchat.ezchat.R;

/**
 * Created by ajo on 04.02.2017.
 */

public class ContactListEntry extends ContactEntry {

    private int status;
    private boolean isOnline;
    private boolean isTyping;
    private String lastMessage;

    public ContactListEntry(ContactEntry entry){
        super(entry.getId(),entry.getUsername(),entry.getName(),entry.getAvatar(),entry.getContactName());
        this.status = R.drawable.icon_offline;
        this.isTyping = false;
        this.isOnline = false;
        this.lastMessage = "";
    }

    public ContactListEntry(ContactEntry entry, int status, boolean isOnline, boolean isTyping){
        super(entry.getId(),entry.getUsername(),entry.getName(),entry.getAvatar(),entry.getContactName());
        this.status = status;
        this.isTyping = isTyping;
        this.isOnline = isOnline;
        this.lastMessage = "";
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
}
