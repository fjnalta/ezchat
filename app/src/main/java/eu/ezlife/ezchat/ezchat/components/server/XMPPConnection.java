package eu.ezlife.ezchat.ezchat.components.server;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import eu.ezlife.ezchat.ezchat.activities.ContactListActivity;

/**
 * Created by ajo on 04.02.2017.
 */

public class XMPPConnection extends AsyncTask<String, String, String> {

    private static AbstractXMPPConnection connection;
    private static String username;
    private static String password;
    private Context context;

    public XMPPConnection(String username, String password, Context context) {
        setUsername(username);
        setPassword(password);
        this.context = context;
    }

    // connect in Background mode
    @Override
    protected String doInBackground(String... params) {
        // Create the configuration for this new connection
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        try {
            configBuilder.setUsernameAndPassword((CharSequence) getUsername(), getPassword());
            configBuilder.setResource("ezChat Android v.0.1");
            configBuilder.setXmppDomain("ezlife.eu");
            configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
            configBuilder.setKeystoreType(null);

            connection = new XMPPTCPConnection(configBuilder.build());

            connection.connect();

            if (connection.isConnected()) {

            }
            connection.login();
            if (connection.isAuthenticated()) {
                Intent contactListActivity = new Intent(context, ContactListActivity.class);
                contactListActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(contactListActivity);
            }

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.w("app", e.toString());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {

    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        XMPPConnection.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        XMPPConnection.password = password;
    }

    public static AbstractXMPPConnection getConnection() {
        return connection;
    }

}