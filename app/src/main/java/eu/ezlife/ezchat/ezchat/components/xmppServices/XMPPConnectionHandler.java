package eu.ezlife.ezchat.ezchat.components.xmppServices;

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

/**
 * Created by ajo on 15.04.17.
 */

public class XMPPConnectionHandler implements ConnectionListener {

    // FireBase UserToken
    private String userToken;

    // Observable List
    private List<XMPPConnectionService> list = new ArrayList<>();

    // XMPP Connection Fields
    private AbstractXMPPConnection connection;
    private boolean connected;
    private boolean loggedin;

    public XMPPConnectionHandler() {
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            userToken = refreshedToken;
            Log.d("TOKEN", userToken);

            // Create the configuration for this new connection
            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            // TODO - get Username and Password from LoginPreferences
            configBuilder.setUsernameAndPassword((CharSequence) username, password);
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
    public void registerObservable(XMPPConnectionService w) {
        list.add(w);
    }

    /**
     * delete one observable from the observable list
     *
     * @param w observable to delete
     */
    public void deleteObservable(XMPPConnectionService w) {
        list.remove(w);
    }

    /**
     * Update all registered observables
     */
    public void updateAllObservables() {
        for (XMPPConnectionService p : list) {
            p.notifyConnectionInterface();
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
        loggedin = true;
        updateAllObservables();
    }

    @Override
    public void connectionClosed() {
        Log.d("xmpp", "ConnectionCLosed!");
        connected = false;
        loggedin = false;
        updateAllObservables();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d("xmpp", "ConnectionClosedOn Error!");
        connected = false;
        loggedin = false;
        updateAllObservables();
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d("xmpp", "ReconnectionSuccessful");
        connected = true;
        loggedin = false;
        updateAllObservables();
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d("xmpp", "Reconnecting " + seconds);
        loggedin = false;
        updateAllObservables();
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.d("xmpp", "ReconnectionFailed!");
        connected = false;
        loggedin = false;
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
                    e.printStackTrace();
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
            e.printStackTrace();
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
    }

    // Connection Status - Getter

    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    public String getUserToken() {
        return userToken;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isLoggedin() {
        return loggedin;
    }
}
