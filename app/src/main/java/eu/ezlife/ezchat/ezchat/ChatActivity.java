package eu.ezlife.ezchat.ezchat;

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

import eu.ezlife.ezchat.ezchat.components.DBDataSource;
import eu.ezlife.ezchat.ezchat.components.XMPPConnection;
import eu.ezlife.ezchat.ezchat.components.adapter.ChatAdapter;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;

public class ChatActivity extends AppCompatActivity implements ChatManagerListener {

    private ChatManager chatManager;
    private ImageView sendButton;
    private EditText chatEdit;
    private Chat myChat;
    private ListView chatHistoryView;
    private ArrayAdapter<ChatHistoryEntry> chatHistoryAdapter;
    private List<ChatHistoryEntry> chatHistory;

    private DBDataSource dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHandler = new DBDataSource(this);

        chatManager = ChatManager.getInstanceFor(XMPPConnection.getConnection());

        dbHandler.open();
        chatHistory = dbHandler.getChatHistory(dbHandler.getContact(getIntent().getStringExtra("EXTRA_USERNAME")).getId());
        dbHandler.close();

        myChat = chatManager.createChat(getIntent().getStringExtra("EXTRA_USERNAME"));
        chatCreated(myChat,true);

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
                newMessage.setTo(cutResourceFromUsername(getIntent().getStringExtra("EXTRA_USERNAME")));
                newMessage.setBody(chatEdit.getText().toString());
                // Create Custom Time Format for DB Sorting
                Calendar c = Calendar.getInstance();
                // Send message through XMPP
                sendMessage(newMessage);
                // Open DB and save Message
                dbHandler.open();
                // create DB-Entry and add Item to chatHistoryList
                chatHistory.add(dbHandler.createMessage(newMessage.getFrom(),
                        newMessage.getTo(),
                        newMessage.getBody(),
                        c.getTime().toString(),
                        dbHandler.getContact(getIntent().getStringExtra("EXTRA_USERNAME")).getId()));
                // Close DB
                dbHandler.close();
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

    public void sendMessage(Message newMessage) {
        if(chatManager != null) {
            try {
                if (XMPPConnection.getConnection().isConnected() && XMPPConnection.getConnection().isAuthenticated()) {
                    myChat.sendMessage(newMessage);
                }
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        myChat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if(message.getBody() != null){
                    Message newMessage = new Message();
                    newMessage.setFrom(cutResourceFromUsername(message.getFrom()));
                    newMessage.setTo(XMPPConnection.getUsername());
                    newMessage.setBody(message.getBody());
                    // Create Custom Time Format for DB Sorting
                    Calendar c = Calendar.getInstance();
                    // Open DB and save Message
                    dbHandler.open();
                    // create DB-Entry and add Item to chatHistoryList
                    chatHistory.add(dbHandler.createMessage(newMessage.getFrom(),
                            newMessage.getTo(),
                            newMessage.getBody(),
                            c.getTime().toString(),
                            dbHandler.getContact(getIntent().getStringExtra("EXTRA_USERNAME")).getId()));
                    // Close DB
                    dbHandler.close();
                    // notify about the changes
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatHistoryAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public String cutResourceFromUsername(String username) {
        String str = username;
        int dotIndex = str.indexOf("@");
        str = str.substring(0, dotIndex);
        return str;
    }
}
