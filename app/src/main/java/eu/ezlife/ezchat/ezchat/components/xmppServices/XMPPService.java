package eu.ezlife.ezchat.ezchat.components.xmppServices;

import eu.ezlife.ezchat.ezchat.components.localSettings.UserPreferences;

/**
 * Created by ajo on 15.04.17.
 */

public interface XMPPService {

    XMPPConnectionHandler connectionHandler = new XMPPConnectionHandler();

    /**
     * update the observables
     */
    void updateMessageObservable();
    void updateConnectionObservable();
}
