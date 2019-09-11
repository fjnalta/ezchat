package eu.ezlife.ezchat.ezchat.data;

import org.jxmpp.jid.Jid;

import java.io.Serializable;

/**
 * Created by ajo on 04.02.2017.
 * Represents a more complex ContactListEntry which includes available information from DB Object ContactEntry
 */
public class ContactListEntry implements Serializable {

    // Basic Attributes
    private Jid jid;
    private int status;
    private boolean isOnline;
    private boolean isTyping;
    private boolean isSubscribed;
    private String lastMessage;

    // DB Attributes
    private String nickName;

    public ContactListEntry(Jid jid, int status, boolean isOnline, boolean isTyping, boolean isSubscribed){
        this.jid = jid;
        this.status = status;
        this.isTyping = isTyping;
        this.isOnline = isOnline;
        this.isSubscribed = isSubscribed;
        this.lastMessage = "-";
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

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public Jid getJid() {
        return jid;
    }

    public void setJid(Jid jid) {
        this.jid = jid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
