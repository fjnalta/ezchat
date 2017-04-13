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
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Calendar;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.adapter.ChatHistoryAdapter;
import eu.ezlife.ezchat.ezchat.components.listener.ListenerService;
import eu.ezlife.ezchat.ezchat.components.server.PushMessageConnection;
import eu.ezlife.ezchat.ezchat.components.server.XMPPConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ChatActivity extends AppCompatActivity implements ListenerService {

    // Serializable input
    private ContactListEntry contact;

    // UI Stuff
    private ImageView sendButton;
    private EditText chatEdit;
    private ListView chatHistoryView;

    // Chat history
    private ArrayAdapter<ChatHistoryEntry> chatHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get Contact Information from Intent
        if (getIntent().getSerializableExtra("ContactListEntry") != null) {
            this.contact = (ContactListEntry) getIntent().getSerializableExtra("ContactListEntry");
        }

        handler.registerObservable(this, getApplicationContext());

        // Set or create current Chat
        try {
            Jid jid = JidCreate.from(contact.getUsername());
            handler.setCurrentChat(handler.getChatManager().chatWith(jid.asEntityBareJidIfPossible()));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        // Set the current Chat history
        handler.getDbHandler().open();
        handler.setChatHistory(handler.getDbHandler().getChatHistory(contact.getId()));
        handler.getDbHandler().close();

        // UI Stuff
        chatHistoryAdapter = new ChatHistoryAdapter(this, handler.getChatHistory());

        chatHistoryView = (ListView) findViewById(R.id.chat_list_view);
        chatHistoryView.setAdapter(chatHistoryAdapter);
        chatEdit = (EditText) findViewById(R.id.chat_edit_text1);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message newMessage = new Message();
                newMessage.setFrom(XMPPConnection.getConnection().getUser().asEntityFullJidIfPossible());
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
                handler.getDbHandler().open();
                // create DB-Entry and add Item to chatHistoryList
                handler.getChatHistory().add(handler.getDbHandler().createMessage(newMessage.getFrom().toString(),
                        newMessage.getTo().toString(),
                        newMessage.getBody(),
                        c.getTime().toString(),
                        handler.getDbHandler().getContact(contact.getUsername()).getId()));
                // Close DB
                handler.getDbHandler().close();
                // Send message through XMPP
                    try {
                        if (XMPPConnection.getConnection().isConnected() && XMPPConnection.getConnection().isAuthenticated()) {
                            handler.getCurrentChat().send(newMessage);
                        }
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                // TODO - send push message to Server
                Log.d("Message TO; FROM: ", newMessage.getTo() + ", " + XMPPConnection.getConnection().getUser().asEntityBareJidString());
                new PushMessageConnection(XMPPConnection.getConnection().getUser().asEntityBareJidString(), newMessage.getTo().toString()).execute("");

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

    /*
     * Call this every time the ChatActivity closes to prevent leaking memory
     */
    @Override
    protected void onStop() {
        super.onStop();
        handler.deleteObservable(this);
    }

    @Override
    public void updateObservable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistoryAdapter.notifyDataSetChanged();
            }
        });
    }
}
