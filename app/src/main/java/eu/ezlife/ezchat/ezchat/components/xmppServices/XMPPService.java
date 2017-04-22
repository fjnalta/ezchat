package eu.ezlife.ezchat.ezchat.components.xmppServices;

/**
 * Created by ajo on 15.04.17.
 */

public interface XMPPService {

    XMPPConnectionHandler connectionHandler = new XMPPConnectionHandler();

    /**
     * update the observables
     */
    public void updateMessageObservable();
    public void updateConnectionObservable();

}
