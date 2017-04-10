package eu.ezlife.ezchat.ezchat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Calendar;
import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.database.DBDataSource;
import eu.ezlife.ezchat.ezchat.components.server.XMPPConnection;
import eu.ezlife.ezchat.ezchat.components.adapter.ChatAdapter;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ChatActivity extends AppCompatActivity implements ChatManagerListener {

    // Serializable input
    private ContactListEntry contact;

    // UI Stuff
    private ImageView sendButton;
    private EditText chatEdit;
    private ListView chatHistoryView;

    // Chat related stuff
    private ChatManager chatManager;
    private Chat myChat;
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
        if(getIntent().getSerializableExtra("ContactListEntry") != null) {
            this.contact = (ContactListEntry) getIntent().getSerializableExtra("ContactListEntry");
        }

        chatManager = ChatManager.getInstanceFor(XMPPConnection.getConnection());
        myChat = chatManager.createChat(contact.getUsername());

        dbHandler = new DBDataSource(this);
        dbHandler.open();
        chatHistory = dbHandler.getChatHistory(dbHandler.getContact(contact.getUsername()).getId());
        dbHandler.close();

        chatCreated(myChat, true);

        chatHistoryAdapter = new ChatAdapter(this, chatHistory);

        chatHistoryView = (ListView) findViewById(R.id.chat_list_view);
        chatHistoryView.setAdapter(chatHistoryAdapter);

        chatEdit = (EditText) findViewById(R.id.chat_edit_text1);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message newMessage = new Message();
                newMessage.setFrom(XMPPConnection.getUsername());
                newMessage.setTo(contact.getName());
                newMessage.setBody(chatEdit.getText().toString());
                // Create Custom Time Format for DB Sorting
                Calendar c = Calendar.getInstance();
                // Open DB and save Message
                dbHandler.open();
                // create DB-Entry and add Item to chatHistoryList
                chatHistory.add(dbHandler.createMessage(newMessage.getFrom(),
                        newMessage.getTo(),
                        newMessage.getBody(),
                        c.getTime().toString(),
                        dbHandler.getContact(contact.getUsername()).getId()));
                // Close DB
                dbHandler.close();
                // Send message through XMPP
                if(myChat != null) {
                    try {
                        if (XMPPConnection.getConnection().isConnected() && XMPPConnection.getConnection().isAuthenticated()) {
                            myChat.sendMessage(newMessage);
                        }
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

                // Create Push Message in Asynchronous Task
//                new PushMessageConnection(newMessage.getFrom(),newMessage.getTo(),"tokenajo").execute("");

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
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {

        myChat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if (message.getBody() != null) {

                    Message newMessage = new Message();
                    newMessage.setFrom(message.getFrom());
                    newMessage.setTo(XMPPConnection.getUsername());
                    newMessage.setBody(message.getBody());
                    // Create Custom Time Format for DB Sorting
                    Calendar c = Calendar.getInstance();
                    // Open DB and save Message
                    dbHandler.open();
                    // create DB-Entry and add Item to chatHistoryList
                    dbHandler.createMessage(newMessage.getFrom(),
                            newMessage.getTo(),
                            newMessage.getBody(),
                            c.getTime().toString(),
                            dbHandler.getContact(contact.getUsername()).getId());
                    // Close DB
                    dbHandler.close();
                }
            }
        });
    }
}
