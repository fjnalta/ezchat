package eu.ezlife.ezchat.ezchat.components.xmppServices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.ezlife.ezchat.ezchat.components.localSettings.UserPreferences;

/**
 * Created by ajo on 15.04.17.
 */

public class XMPPConnectionHandler implements ConnectionListener {

    // UserPreferences
    private UserPreferences prefs = null;

    // FireBase UserToken
    private String userToken;

    // Observable List
    private List<XMPPService> list = new ArrayList<>();

    // XMPP Connection Fields
    private static AbstractXMPPConnection connection;
    private boolean connected;
    private boolean loggedIn;

    // MyMessageHandler
    private XMPPMessageHandler messageHandler = null;


    public XMPPConnectionHandler() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        userToken = refreshedToken;
        Log.d("TOKEN", userToken);

    }

    public void buildConnection() {
        try {
            // Create the configuration for this new connection
            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            configBuilder.setUsernameAndPassword((CharSequence) prefs.getPrefUserName(), prefs.getPrefPassword());
            configBuilder.setResource("ezChat Android v.0.1");
            configBuilder.setXmppDomain("ezlife.eu");
            configBuilder.setResource("ezChat");
            configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
            configBuilder.setKeystoreType(null);

            connection = new XMPPTCPConnection(configBuilder.build());
            connection.addConnectionListener(this);

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // Private Interface Methods
    // -------------------------

    /**
     * Add one ObservableClass to the logicHandler
     *
     * @param w the Observable to add
     */
    public void registerObservable(XMPPService w, Context context) {
        list.add(w);
        if (this.prefs == null) {
            prefs = new UserPreferences(context);
        }
    }

    /**
     * delete one observable from the observable list
     *
     * @param w observable to delete
     */
    public void deleteObservable(XMPPService w) {
        list.remove(w);
    }

    /**
     * Update all registered observables
     */
    public void updateAllObservables() {
        for (XMPPService p : list) {
            p.updateConnectionObservable();
        }
    }

    // ---------------------------
    // Connection Listener Methods
    // ---------------------------

    @Override
    public void connected(XMPPConnection connection) {
        Log.d("xmpp", "Connected!");
        connected = true;
        if (!connection.isAuthenticated()) {
            login();
        }
        updateAllObservables();
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d("xmpp", "Authenticated!");
        loggedIn = true;
        this.messageHandler = new XMPPMessageHandler();
        // Update FireBase Id

        updateAllObservables();
    }

    @Override
    public void connectionClosed() {
        Log.d("xmpp", "ConnectionCLosed!");
        connected = false;
        loggedIn = false;
        updateAllObservables();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d("xmpp", "ConnectionClosedOn Error!");
        connected = false;
        loggedIn = false;
        updateAllObservables();
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d("xmpp", "ReconnectionSuccessful");
        connected = true;
        loggedIn = false;
        updateAllObservables();
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d("xmpp", "Reconnecting " + seconds);
        loggedIn = false;
        updateAllObservables();
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.d("xmpp", "ReconnectionFailed!");
        connected = false;
        loggedIn = false;
        updateAllObservables();
    }

    // Initialize
    public void connectConnection() {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... arg0) {
                try {
                    connection.connect();
                } catch (IOException | SmackException | XMPPException | InterruptedException e) {
                    Log.d("xmpp", "Already connected, try login");
                    login();
                }
                return null;
            }
        };
        connectionThread.execute();
    }

    // Login
    public void login() {
        try {
            connection.login();
        } catch (XMPPException | SmackException | IOException e) {

            Log.d("xmpp", "ConnectionFailed!");
            updateAllObservables();
        } catch (Exception e) {
        }
    }

    // Disconnect Function
    public void disconnectConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
        connected = false;
        loggedIn = false;
    }

    // Connection Status - Getter
    public static AbstractXMPPConnection getConnection() {
        return connection;
    }

    public String getUserToken() {
        return userToken;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public XMPPMessageHandler getMessageHandler() {
        return messageHandler;
    }


    public void setCredentials(String username, String password) {
        prefs.setCredentials(username, password);
    }

    public UserPreferences getPrefs() {
        return prefs;
    }
}
