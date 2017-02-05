package eu.ezlife.ezchat.ezchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import eu.ezlife.ezchat.ezchat.components.myXMPPConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistory;

public class ChatActivity extends AppCompatActivity implements ChatManagerListener {

    private ChatManager chatManager;
    private ImageView sendButton;
    private EditText chatEdit;
    private Chat myChat;
    private ListView chatHistoryView;
    private ArrayAdapter<ChatHistory> chatHistoryAdapter;
    private List<ChatHistory> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

 //       Toast.makeText(getApplicationContext(), getIntent().getStringExtra("EXTRA_USERNAME"), Toast.LENGTH_SHORT).show();
        chatManager = ChatManager.getInstanceFor(myXMPPConnection.getConnection());

        myChat = chatManager.createChat(getIntent().getStringExtra("EXTRA_USERNAME"));
        chatCreated(myChat,true);

        chatHistory = new ArrayList<ChatHistory>();
        chatHistoryAdapter = new ArrayAdapter<ChatHistory>(getApplicationContext(),android.R.layout.simple_list_item_1,chatHistory);
        chatHistoryView = (ListView) findViewById(R.id.chat_list_view);

        chatHistoryView.setAdapter(chatHistoryAdapter);

        chatEdit = (EditText) findViewById(R.id.chat_edit_text1);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message newMessage = new Message();
                newMessage.setFrom(myXMPPConnection.getUsername());
                newMessage.setBody(chatEdit.getText().toString());
                chatHistory.add(new ChatHistory(newMessage.getFrom(),newMessage.getBody()));

                sendMessage(getIntent().getStringExtra("EXTRA_USERNAME"),newMessage);
                chatEdit.setText("");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatHistoryAdapter.notifyDataSetChanged();
                    }
                });
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
        else {
            Log.d("ChatActivity","chatmanager is null");
        }
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        myChat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if(message.getBody() != null){
                    Message newMessage = new Message();
                    newMessage.setBody(message.getBody());
                    newMessage.setFrom(message.getFrom());
                    chatHistory.add(new ChatHistory(newMessage.getFrom(),newMessage.getBody()));

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
}
