package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 04.02.2017.
 */

public class ContactListEntry {

    private String username;
    private String name;
    private String contactName;
    private String status;
    private String resource;
    private int isOnline;
    private int isTyping;
    private int avatar;

    public ContactListEntry(String username, String status, String name){
        this.username = username;
        this.name = name;
        this.contactName = name;
        this.status = status;
        this.resource = "-";
        this.isTyping = 0;
        this.avatar = 0;
        this.isOnline = 0;
    }

    public ContactListEntry(String username, String name, String status, int isOnline, int avatar, int isTyping, String resource) {
        this.username = username;
        this.name = name;
        this.contactName = name;
        this.status = status;
        this.isOnline = isOnline;
        this.avatar = avatar;
        this.isTyping = isTyping;
        this.resource = resource;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getIsTyping() {
        return isTyping;
    }

    public void setIsTyping(int isTyping) {
        this.isTyping = isTyping;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return getUsername() + "\n" + getStatus() ;
    }
}
