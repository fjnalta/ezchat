package eu.ezlife.ezchat.ezchat.components.xmppServices;

import android.annotation.SuppressLint;
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
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.database.DBDataSource;
import eu.ezlife.ezchat.ezchat.components.restServices.TokenRegistrationConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;
import eu.ezlife.ezchat.ezchat.data.ObserverObject;

/**
 * Created by ajo on 26.04.17.
 */

public class XMPPHandler extends Observable implements ConnectionListener, IncomingChatMessageListener, RosterListener, RosterLoadedListener {

    // XMPP Connection Fields
    public static AbstractXMPPConnection connection = null;
    public boolean connected = false;
    public boolean loggedIn = false;

    // Chat
    private ChatManager chatManager = null;
    private List<ChatHistoryEntry> chatHistory = new ArrayList<>();
    private Chat currentChat = null;

    // Contact List
    private Roster roster = null;
    private List<ContactListEntry> contactList = new ArrayList<>();

    // Database
    private DBDataSource dbHandler = null;

    /**
     * Connect Method
     *
     * @param username the username to login
     * @param password the password
     */
    public void buildConnection(String username, String password) {
        try {
            // Create the configuration for this new connection
            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            configBuilder.setUsernameAndPassword((CharSequence) username, password);
            configBuilder.setResource("ezChat Android v.0.1");
            configBuilder.setXmppDomain("ezlife.eu");
            configBuilder.setResource("ezChat");
            configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
            configBuilder.setKeystoreType(null);

            connection = new XMPPTCPConnection(configBuilder.build());
            connection.addConnectionListener(this);
            connect();

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... arg0) {
                try {
                    connection.connect();
                } catch (IOException | SmackException | XMPPException | InterruptedException e) {
                    Log.d("xmpp", "Already connected, try login");
                    if(connection.isConnected()) {
                        login();
                    }
                }
                return null;
            }
        };
        connectionThread.execute();
    }

    /**
     * Automatically login after connecting
     */
    public void login() {
        try {
            connection.login();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.d("xmpp", "LoginFailed!");
        }
    }

    /**
     * Disconnect Method
     */
    public void disconnectConnection() {
        Log.d("xmpp", "Disconnecting");
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
        connected = false;
        loggedIn = false;
    }

    /**
     * Gets the Android Context and loads the
     * Database Handler and User Preferences with it.
     *
     * @param ctx the Android Application Context
     */
    public void setAndroidContext(Context ctx) {
        dbHandler = new DBDataSource(ctx);
    }

    // Chat Methods
    // ------------
    public Chat getCurrentChat() {
        return currentChat;
    }

    public void setCurrentChat(Chat currentChat) {
        this.currentChat = currentChat;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public List<ChatHistoryEntry> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChatHistoryEntry> chatHistory) {
        this.chatHistory = chatHistory;
    }

    // Roster Methods
    // --------------
    public List<ContactListEntry> getContactList() {
        return contactList;
    }

    // Helper Methods
    // --------------

    /**
     * Helper method to create visual status icons from
     * the user presence
     *
     * @param presence Presence to evaluate
     */
    private int evaluateContactStatus(Presence presence) {
        int stat = R.drawable.icon_offline;
        if (presence.isAvailable()) {
            if (presence.getMode().name().equals("available")) {
                stat = R.drawable.icon_online;
            }
            if (presence.getMode().name().equals("away")) {
                stat = R.drawable.icon_away;
            }
            if (presence.getMode().name().equals("dnd")) {
                stat = R.drawable.icon_dnd;
            }
        } else {
            stat = R.drawable.icon_offline;
        }
        return stat;
    }

    /**
     * Updates the Status of a @ContactListEntry
     *
     * @param presence
     */
    private void updateContact(Presence presence) {
        for (ContactListEntry entry : contactList) {
            if (presence.getFrom().asBareJid().toString().equals(entry.getUsername())) {
                Log.d("presence", "called found");
                entry.setStatus(evaluateContactStatus(presence));
            }
        }
    }


    // ------------------------
    // XMPP Connection Listener
    // ------------------------
    @Override
    public void connected(XMPPConnection connection) {
        Log.d("xmpp", "Connected!");
        connected = true;
        setChanged();
        notifyObservers();

        if (!connection.isAuthenticated()) {
            login();
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d("xmpp", "Authenticated!");
        loggedIn = true;

        // activate Message Listener
        chatManager = ChatManager.getInstanceFor(this.connection);
        chatManager.addIncomingListener(this);

        // activate Roster Listener
        roster = Roster.getInstanceFor(this.connection);
        roster.addRosterLoadedListener(this);
        roster.addRosterListener(this);

        // TODO - AsncTask -.-
        // Update FireBase Id
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        prefs.setPrefFireBaseToken(refreshedToken);

        // Register REST FireBaseId
//        new TokenRegistrationConnection(prefs.getPrefFireBaseToken(), connection.getUser().asEntityBareJidString()).execute("");

        setChanged();
        notifyObservers(new ObserverObject("authenticated"));
    }

    @Override
    public void connectionClosed() {
        Log.d("xmpp", "ConnectionCLosed!");
        connected = false;
        loggedIn = false;
        setChanged();
        notifyObservers();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d("xmpp", "ConnectionClosedOn Error!");
        connected = false;
        loggedIn = false;
        setChanged();
        notifyObservers();
    }

    public void reconnectionSuccessful() {
        Log.d("xmpp", "ReconnectionSuccessful");
        connected = true;
        loggedIn = false;
        setChanged();
        notifyObservers();
    }

    public void reconnectingIn(int seconds) {
        Log.d("xmpp", "Reconnecting " + seconds);
        loggedIn = false;
        setChanged();
        notifyObservers();
    }

    public void reconnectionFailed(Exception e) {
        Log.d("xmpp", "ReconnectionFailed!");
        connected = false;
        loggedIn = false;
        setChanged();
        notifyObservers();
    }

    // -------------
    // XMPP Messages
    // -------------

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        if (message.getBody() != null) {
            Message newMessage = new Message();
            newMessage.setTo(message.getTo());
            newMessage.setBody(message.getBody());
            // Create Custom Time Format for DB Sorting
            Calendar c = Calendar.getInstance();
            // Open DB and save Message
            dbHandler.open();
            // create DB-Entry
            ChatHistoryEntry curEntry = dbHandler.createMessage(from.asEntityBareJidString(),
                    newMessage.getTo().toString(),
                    newMessage.getBody(),
                    c.getTime().toString(),
                    dbHandler.getContact(from.asEntityBareJidString()).getId());
            // Set the last message in contactList
            for (ContactListEntry entry : contactList) {
                if (entry.getUsername().equals(from.asEntityBareJidString())) {
                    entry.setLastMessage(newMessage.getBody());
                }
            }
            // Add message to chat if the chat is active
            if (currentChat == chat) {
                chatHistory.add(curEntry);
            }
            // Close DB
            dbHandler.close();

            setChanged();
            notifyObservers();
        }
    }

    // -----------
    // XMPP Roster
    // -----------

    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        Log.d("ROSTER", "entries added");
        setChanged();
        notifyObservers();
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        Log.d("ROSTER", "entries updated");
        setChanged();
        notifyObservers();
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        Log.d("ROSTER", "entries deleted");
        setChanged();
        notifyObservers();
    }

    @Override
    public void presenceChanged(Presence presence) {
        Log.d("ROSTER", "presence changed");
        updateContact(presence);
        setChanged();
        notifyObservers();
    }

    // ------------------
    // XMPP Roster Loaded
    // ------------------

    @Override
    public void onRosterLoaded(final Roster roster) {
        Log.d("ROSTER", "load contact list");
        /*final Collection<RosterEntry> rosterEntries = roster.getEntries();

        AsyncTask<Void, Void, Boolean> rosterThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                dbHandler.open();
                for (RosterEntry entry : rosterEntries) {
                    if (!dbHandler.contactExists(entry.getJid().toString())) {
                        dbHandler.createContact(entry.getJid().toString(), R.mipmap.ic_launcher, entry.getName());
                        Log.d("ROSTER", "contact created");
                    }
                }
                dbHandler.close();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                contactList.clear();
                dbHandler.open();
                for (RosterEntry entry : rosterEntries) {

                    Presence presence = roster.getPresence(entry.getJid());
                    ContactListEntry contactEntry = new ContactListEntry(dbHandler.getContact(entry.getJid().toString()),evaluateContactStatus(presence), presence.isAvailable(), false);
                    contactEntry.setLastMessage(dbHandler.getLastMessage(contactEntry.getUsername()));
                    contactList.add(contactEntry);
                }

                dbHandler.close();

                setChanged();
                notifyObservers();

            }
        };
        rosterThread.execute();*/
    }

    @Override
    public void onRosterLoadingFailed(Exception exception) {
        Log.d("ROSTER", "Loading Failed");
    }
}
