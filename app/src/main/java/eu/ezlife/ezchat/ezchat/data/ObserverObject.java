package eu.ezlife.ezchat.ezchat.data;

import org.jxmpp.jid.Jid;

/**
 * Created by ajo on 26.04.17.
 */

public class ObserverObject {

    private String text = "";

    public ObserverObject(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}