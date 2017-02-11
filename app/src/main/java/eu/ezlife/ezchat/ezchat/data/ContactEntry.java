package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 09.02.2017.
 */

/*
* Represents POJO from DB-Contact
* */
public class ContactEntry {

    private long id;
    private String username;
    private String name;
    private String contactName;
    private int avatar;

    public ContactEntry(long id, String username, String name, int avatar) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.avatar = avatar;
    }

    public ContactEntry(long id, String username, String name, int avatar, String contactName) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.avatar = avatar;
        this.contactName = contactName;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
