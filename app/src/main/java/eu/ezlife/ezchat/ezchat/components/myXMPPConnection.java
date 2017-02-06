package eu.ezlife.ezchat.ezchat.components;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 04.02.2017.
 */

public class myXMPPConnection extends AsyncTask<String, String, String> {

    private static AbstractXMPPConnection connection;
    private static String username;
    private static String password;

    private static Collection<RosterEntry> rosterEntries;
    private static List<ContactListEntry> contactList = new ArrayList<ContactListEntry>();

    public myXMPPConnection(String username, String password) {
        setUsername(username);
        setPassword(password);
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
                updateContactList();
            }
        } catch (Exception e) {
            Log.w("app", e.toString());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {

    }

    // Setter + Getter
    // ---------------
    public static void setRosterEntries(Collection<RosterEntry> rosterEntries) {
        myXMPPConnection.rosterEntries = rosterEntries;
    }

    public static List<ContactListEntry> getContactList(){
        return contactList;
    }

    public static void updateContactList(){
        try {
            Roster roster = Roster.getInstanceFor(connection);
            if (!roster.isLoaded())
                roster.reloadAndWait();
            setRosterEntries(roster.getEntries());

            Presence presence;
            contactList.clear();
            for (RosterEntry entry : rosterEntries) {
                presence = roster.getPresence(entry.getUser());
                contactList.add(new ContactListEntry(entry.getUser(),presence.getType().name(),entry.getName()));
            }
        } catch (Exception e) {
            Log.w("app", e.toString());
        }
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        myXMPPConnection.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        myXMPPConnection.password = password;
    }

    public static AbstractXMPPConnection getConnection() {
        return connection;
    }

}
