package eu.ezlife.ezchat.ezchat;

import android.icu.text.SimpleDateFormat;
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

import java.util.Date;
import java.util.List;

import eu.ezlife.ezchat.ezchat.components.myDBDataSource;
import eu.ezlife.ezchat.ezchat.components.myXMPPConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;

public class ChatActivity extends AppCompatActivity implements ChatManagerListener {

    private ChatManager chatManager;
    private ImageView sendButton;
    private EditText chatEdit;
    private Chat myChat;
    private ListView chatHistoryView;
    private ArrayAdapter<ChatHistoryEntry> chatHistoryAdapter;
    private List<ChatHistoryEntry> chatHistoryEntry;

    private myDBDataSource dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHandler = new myDBDataSource(this);
        chatManager = ChatManager.getInstanceFor(myXMPPConnection.getConnection());

        myChat = chatManager.createChat(getIntent().getStringExtra("EXTRA_USERNAME"));
        chatCreated(myChat,true);

        chatHistoryAdapter = new ArrayAdapter<ChatHistoryEntry>(getApplicationContext(),android.R.layout.simple_list_item_1, chatHistoryEntry);
        chatHistoryView = (ListView) findViewById(R.id.chat_list_view);

        chatHistoryView.setAdapter(chatHistoryAdapter);

        chatEdit = (EditText) findViewById(R.id.chat_edit_text1);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message newMessage = new Message();
                newMessage.setFrom(myXMPPConnection.getUsername());
                newMessage.setTo(cutResourceFromUsername(getIntent().getStringExtra("EXTRA_USERNAME")));
                newMessage.setBody(chatEdit.getText().toString());


                // Create Custom Time Format for DB Sorting
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.getDateTimeInstance().format(new Date());
                // Open DB and save Message
                dbHandler.open();
                sdf = new SimpleDateFormat("HH:MM");
                // create DB-Entry and add Item to chatHistoryList
                chatHistoryEntry.add(dbHandler.createMessage(newMessage.getFrom(),
                        newMessage.getTo(),
                        newMessage.getBody(),
                        sdf.getDateTimeInstance().format(new Date()),
                        dbHandler.getUser(getIntent().getStringExtra("EXTRA_USERNAME")).getContactsId()));
                // Close DB
                dbHandler.close();
                // Send message through XMPP
                sendMessage(getIntent().getStringExtra("EXTRA_USERNAME"),newMessage);
                // Reset TextBox
                chatEdit.setText("");
            }
        });
    }

    public void sendMessage(String username, Message newMessage) {
        if(chatManager!=null) {
            try {
                if (myXMPPConnection.getConnection().isConnected() && myXMPPConnection.getConnection().isAuthenticated()) {
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
                    newMessage.setTo(myXMPPConnection.getUsername());
                    newMessage.setBody(message.getBody());
                    // Create Custom Time Format for DB Sorting
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.getDateTimeInstance().format(new Date());
                    // Open DB and save Message
                    dbHandler.open();
                    sdf = new SimpleDateFormat("HH:MM");
                    // create DB-Entry and add Item to chatHistoryList
                    chatHistoryEntry.add(dbHandler.createMessage(newMessage.getFrom(),
                            newMessage.getTo(),
                            newMessage.getBody(),
                            sdf.getDateTimeInstance().format(new Date()),
                            dbHandler.getUser(getIntent().getStringExtra("EXTRA_USERNAME")).getContactsId()));
                    // Close DB
                    dbHandler.close();

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
