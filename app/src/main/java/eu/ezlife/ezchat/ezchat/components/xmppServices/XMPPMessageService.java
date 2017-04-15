package eu.ezlife.ezchat.ezchat.components.xmppServices;

/**
 * Created by ajo on 11.04.17.
 */

public interface XMPPMessageService {

    XMPPMessageHandler handler = new XMPPMessageHandler();

    /**
     * update the observables
     */
    public void updateObservable();
}
