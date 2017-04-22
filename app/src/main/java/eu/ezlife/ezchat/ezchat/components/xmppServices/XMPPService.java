package eu.ezlife.ezchat.ezchat.components.xmppServices;

/**
 * Created by ajo on 15.04.17.
 */

public interface XMPPConnectionService {

    XMPPConnectionHandler connectionHandler = new XMPPConnectionHandler();
    XMPPMessageHandler messageHandler = new XMPPMessageHandler();

    /**
     * update the observables
     */
    public void updateMessageObservable();
    public void updateConnectionObservable();

}
