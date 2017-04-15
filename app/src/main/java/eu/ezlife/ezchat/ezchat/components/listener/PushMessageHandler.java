package eu.ezlife.ezchat.ezchat.components.listener;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import eu.ezlife.ezchat.ezchat.components.database.DBDataSource;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 11.04.17.
 */

public class PushMessageHandler implements XMPPConnectionService {

    // Observable List
    private List<PushMessageService> list = new ArrayList<>();
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

    public PushMessageHandler() {
        // Instantiate the Contact List
        // TODO - remove connectionHandler from this Class
        roster = Roster.getInstanceFor(connectionHandler.getConnection());
        // Instantiate ChatManager for all Chats
        chatManager = ChatManager.getInstanceFor(connectionHandler.getConnection());

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
                    if (currentChat == chat) {
                        Log.d("Chat: ", "We are in the incoming chat BOYYY");
                        chatHistory.add(dbHandler.createMessage(from.asEntityBareJidString(),
                                newMessage.getTo().toString(),
                                newMessage.getBody(),
                                c.getTime().toString(),
                                dbHandler.getContact(from.asEntityBareJidString()).getId()));
                    } else {
                        dbHandler.createMessage(from.asEntityBareJidString(),
                                newMessage.getTo().toString(),
                                newMessage.getBody(),
                                c.getTime().toString(),
                                dbHandler.getContact(from.asEntityBareJidString()).getId());
                    }
                    // Close DB
                    dbHandler.close();

                    updateAllObservables();
                }
            }
        });

        // Contact List Listener
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {
                updateAllObservables();
            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {
                updateAllObservables();
            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {
                updateAllObservables();
            }

            // Ignored events public void entriesAdded(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                updateAllObservables();
            }
        });

    }

    /**
     * Add one ObservableClass to the logicHandler
     * @param w the Observable to add
     */
    public void registerObservable(PushMessageService w, Context context) {
        list.add(w);
        if (this.context == null) {
            this.context = context;
            dbHandler = new DBDataSource(context);
        }
    }

    /**
     * delete one observable from the observable list
     * @param w observable to delete
     */
    public void deleteObservable(PushMessageService w) {
        list.remove(w);
    }

    /**
     * Update all registered observables
     */
    public void updateAllObservables() {
        for (PushMessageService p : list) {
            p.updateObservable();
        }
    }

    public DBDataSource getDbHandler() {
        return dbHandler;
    }

    public Roster getRoster() {
        return roster;
    }

    public List<ContactListEntry> getContactList() {
        return contactList;
    }

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

    @Override
    public void notifyConnectionInterface() {

    }
}
