package eu.ezlife.ezchat.ezchat.data;

/**
 * Created by ajo on 04.02.2017.
 */

public class ContactListEntry {

    private String name;
    private String status;
    private String username;

    public ContactListEntry(String name, String status, String username){
        this.name = name;
        this.status = status;
        this.username = username;
    }

    // Setter + Getter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return getName() + " - " + getStatus() + "\n" + getName();
    }
}
