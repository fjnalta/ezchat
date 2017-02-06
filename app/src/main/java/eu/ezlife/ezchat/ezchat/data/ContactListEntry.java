package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 04.02.2017.
 */

public class ContactListEntry {

    private String contactName;
    private String username;
    private String lastMessage;
    private String status;
    private boolean isTyping;
    private int iconID;

    public ContactListEntry(String username, String status, String contactName){
        this.username = username;
        this.lastMessage = " - ";
        this.status = status;
        this.isTyping = false;
        this.iconID = 0;
        this.contactName = contactName;
    }

    // Setter + Getter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String toString() {
        return getUsername() + "\n" + getStatus() ;
    }
}
