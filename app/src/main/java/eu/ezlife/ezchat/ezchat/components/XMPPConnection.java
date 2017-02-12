package eu.ezlife.ezchat.ezchat.components;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

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
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setServiceName("ezlife.eu")
                .setHost("ezlife.eu")
                .setPort(5222)
                .setUsernameAndPassword(getUsername(), getPassword())
                .setCompressionEnabled(false)
                .setResource("ezChat Android ALPHA v.0.1")
                .setDebuggerEnabled(true)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                .build();

        connection = new XMPPTCPConnection(config);
        try {
            connection.connect();

            if (connection.isConnected()) {

            }
            connection.login();
            if (connection.isAuthenticated()) {
                Intent contactListActivity = new Intent(context, eu.ezlife.ezchat.ezchat.ContactListActivity.class);
                contactListActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(contactListActivity);
            }
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