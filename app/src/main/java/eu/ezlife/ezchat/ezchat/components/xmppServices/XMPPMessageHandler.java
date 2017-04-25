package eu.ezlife.ezchat.ezchat.components.xmppServices;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.database.DBDataSource;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 11.04.17.
 */
public class XMPPMessageHandler {

    // Observable List
    private List<XMPPService> list = new ArrayList<>();
    private Context context = null;

    // Contact List
    private Roster roster = null;
    private List<ContactListEntry> contactList = new ArrayList<>();

    // Chat
    private ChatManager chatManager = null;
    private List<ChatHistoryEntry> chatHistory = new ArrayList<>();
    private Chat currentChat = null;

    // Database
    private DBDataSource dbHandler = null;

    public XMPPMessageHandler(Context ctx) {
        this.context = ctx;
        dbHandler = new DBDataSource(context);
        // Instantiate the Contact List
        roster = Roster.getInstanceFor(XMPPConnectionHandler.getConnection());
        // Instantiate ChatManager for all Chats
        chatManager = ChatManager.getInstanceFor(XMPPConnectionHandler.getConnection());

        // Message Listener
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
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
                    if(!message.getBody().equals(dbHandler.getLastMessage(from.asEntityBareJidString()))) {
                        Log.d("incMsg",message.getBody());

                        ChatHistoryEntry curEntry = dbHandler.createMessage(from.asEntityBareJidString(),
                                newMessage.getTo().toString(),
                                newMessage.getBody(),
                                c.getTime().toString(),
                                dbHandler.getContact(from.asEntityBareJidString()).getId());
                        Log.d("incMsg",dbHandler.getLastMessage(from.asEntityBareJidString()));

                        if (currentChat == chat) {
                            Log.d("incMsg","the chat");
                            chatHistory.add(curEntry);
                        }
                    }
                    // Close DB
                    dbHandler.close();

                    loadContactList();
                    updateAllObservables();
                }
            }
        });

        roster.addRosterLoadedListener(new RosterLoadedListener() {
            @Override
            public void onRosterLoaded(Roster roster) {

                Log.d("ROSTER", "entries loaded");
                Collection<RosterEntry> rosterEntries = roster.getEntries();
                dbHandler.open();
                Presence presence;

                for (RosterEntry entry : rosterEntries) {
                    // update contactListDB
                    if (dbHandler.getContact(entry.getUser()) == null) {
                        dbHandler.createContact(entry.getUser(), R.mipmap.ic_launcher, entry.getName());
                    }
                }
                dbHandler.close();
                loadContactList();
                updateAllObservables();
            }

            @Override
            public void onRosterLoadingFailed(Exception exception) {

            }
        });

        // Contact List Listener
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {
                Log.d("ROSTER", "entries added");
                updateAllObservables();
            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {
                Log.d("ROSTER", "entries updated");
                updateAllObservables();
            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {
                Log.d("ROSTER", "entries deleted");
                updateAllObservables();
            }

            // Ignored events public void entriesAdded(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                Log.d("ROSTER", "presence changed");
                updateContact(presence);
                updateAllObservables();
            }
        });

    }

    /**
     * Add one ObservableClass to the logicHandler
     *
     * @param w the Observable to add
     */
    public void registerObservable(XMPPService w, Context context) {
        list.add(w);
        if (this.context == null) {
            this.context = context;
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
            p.updateMessageObservable();
        }
    }

    public DBDataSource getDbHandler() {
        return dbHandler;
    }

    // Roster
    public List<ContactListEntry> getContactList() {
        return contactList;
    }

    private void loadContactList() {
        Log.d("XMPPMessageHandler","Loading Contact List");
        dbHandler.open();
        Collection<RosterEntry> rosterEntries = roster.getEntries();
        Presence presence;
        contactList.clear();
        for (RosterEntry entry : rosterEntries) {
            // create contactList Object
            presence = roster.getPresence(entry.getJid());
            ContactListEntry currentEntry = new ContactListEntry(dbHandler.getContact(entry.getUser()), evaluateContactStatus(presence), presence.isAvailable(), false);
            currentEntry.setLastMessage(dbHandler.getLastMessage(entry.getUser()));
            contactList.add(currentEntry);
        }
        dbHandler.close();

    }

    private void updateContact(Presence presence) {
        for(ContactListEntry entry : contactList) {
            if(presence.getFrom().asBareJid().toString().equals(entry.getUsername())) {
                Log.d("presence","called found");
                entry.setStatus(evaluateContactStatus(presence));
            }
        }
    }

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


    // ChatMessages
    public ChatManager getChatManager() {
        return chatManager;
    }

    public Chat getCurrentChat() {
        return currentChat;
    }

    public void setCurrentChat(Chat currentChat) {
        this.currentChat = currentChat;
    }

    public List<ChatHistoryEntry> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChatHistoryEntry> chatHistory) {
        this.chatHistory = chatHistory;
    }

    // TODO - clean this shit
    private String cutDomainFromUsername(String username) {
        String str = username;
        int dotIndex = str.indexOf("@");
        str = str.substring(0, dotIndex);
        return str;
    }
}
