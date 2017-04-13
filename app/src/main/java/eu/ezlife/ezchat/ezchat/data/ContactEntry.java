package eu.ezlife.ezchat.ezchat.data;

import java.io.Serializable;

/**
 * Created by ajo on 09.02.2017.
 * Represents plain old java object from DB-Contact
 * Object is serializable because it is passed between ContactListActivity and ChatActivity
 */
public class ContactEntry implements Serializable {

    private long id;
    private String username;
    private String contactName;
    private int avatar;

    public ContactEntry(long id, String username, int avatar, String contactName) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.contactName = contactName;
    }

    /**
     * Setter + Getter
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

}
