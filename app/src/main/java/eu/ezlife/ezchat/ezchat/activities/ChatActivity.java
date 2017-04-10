package eu.ezlife.ezchat.ezchat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Calendar;
import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.adapter.ChatHistoryAdapter;
import eu.ezlife.ezchat.ezchat.components.database.DBDataSource;
import eu.ezlife.ezchat.ezchat.components.server.XMPPConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ChatActivity extends AppCompatActivity {

    // Serializable input
    private ContactListEntry contact;

    // UI Stuff
    private ImageView sendButton;
    private EditText chatEdit;
    private ListView chatHistoryView;

    // Chat related stuff
    private Chat myChat;
    private ChatManager chatManager;
    // Chat history
    private ArrayAdapter<ChatHistoryEntry> chatHistoryAdapter;
    private List<ChatHistoryEntry> chatHistory;

    // Database
    private DBDataSource dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get Contact Information from Intent
        if (getIntent().getSerializableExtra("ContactListEntry") != null) {
            this.contact = (ContactListEntry) getIntent().getSerializableExtra("ContactListEntry");
        }
        // Load Chat History
        dbHandler = new DBDataSource(this);
        dbHandler.open();
        chatHistory = dbHandler.getChatHistory(dbHandler.getContact(contact.getUsername()).getId());
        for (ChatHistoryEntry curentry : chatHistory) {
            Log.d("kennsch", "From: " + curentry.getFrom() + " To: " + curentry.getTo());
        }

        dbHandler.close();

        // UI Stuff
        chatHistoryAdapter = new ChatHistoryAdapter(this, chatHistory);

        chatHistoryView = (ListView) findViewById(R.id.chat_list_view);
        chatHistoryView.setAdapter(chatHistoryAdapter);
        chatEdit = (EditText) findViewById(R.id.chat_edit_text1);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message newMessage = new Message();
                newMessage.setFrom(XMPPConnection.getConnection().getUser());

                Jid myUsername = null;
                try {
                    myUsername = JidCreate.from(contact.getUsername());
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                newMessage.setTo(myUsername);
                newMessage.setBody(chatEdit.getText().toString());
                // Create Custom Time Format for DB Sorting
                Calendar c = Calendar.getInstance();
                // Open DB and save Message
                dbHandler.open();
                // create DB-Entry and add Item to chatHistoryList
                chatHistory.add(dbHandler.createMessage(newMessage.getFrom().toString(),
                        newMessage.getTo().toString(),
                        newMessage.getBody(),
                        c.getTime().toString(),
                        dbHandler.getContact(contact.getUsername()).getId()));
                // Close DB
                dbHandler.close();
                // Send message through XMPP
                if (myChat != null) {
                    try {
                        if (XMPPConnection.getConnection().isConnected() && XMPPConnection.getConnection().isAuthenticated()) {
                            myChat.send(newMessage);
                        }
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Create Push Message in Asynchronous Task
//                new PushMessageConnection(newMessage.getFrom(),newMessage.getTo()).execute("");
                // Reset TextBox
                chatEdit.setText("");
                // notify about the changes
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatHistoryAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        chatManager = ChatManager.getInstanceFor(XMPPConnection.getConnection());
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                if (message.getBody() != null) {

                    Message newMessage = new Message();
                    newMessage.setFrom(message.getFrom());
                    newMessage.setTo(message.getTo());
                    newMessage.setBody(message.getBody());
                    // Create Custom Time Format for DB Sorting
                    Calendar c = Calendar.getInstance();
                    // Open DB and save Message
                    dbHandler.open();
                    // create DB-Entry and add Item to chatHistoryList
                    chatHistory.add(dbHandler.createMessage(newMessage.getFrom().toString(),
                            newMessage.getTo().toString(),
                            newMessage.getBody(),
                            c.getTime().toString(),
                            dbHandler.getContact(contact.getUsername()).getId()));
                    // Close DB
                    dbHandler.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // notify about the changes
                            chatHistoryAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        try {
            Jid jid = JidCreate.from(contact.getUsername());
            myChat = chatManager.chatWith(jid.asEntityBareJidIfPossible());
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }



    }
}
