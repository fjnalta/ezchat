package eu.ezlife.ezchat.ezchat.components.xmppServices;

/**
 * Created by ajo on 15.04.17.
 */

public interface XMPPConnectionService {

    XMPPConnectionHandler connectionHandler = new XMPPConnectionHandler();

    public void notifyConnectionInterface();

}
