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
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService;
import eu.ezlife.ezchat.ezchat.components.restServices.PushMessageConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ChatActivity extends AppCompatActivity implements XMPPService {

    // Serializable intent
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

        connectionHandler.getMessageHandler().registerObservable(this, getApplicationContext());

        // Set or create current Chat
        try {
            Jid jid = JidCreate.from(contact.getUsername());
            connectionHandler.getMessageHandler().setCurrentChat(connectionHandler.getMessageHandler().getChatManager().chatWith(jid.asEntityBareJidIfPossible()));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        // Set the current Chat history
        connectionHandler.getMessageHandler().getDbHandler().open();
        connectionHandler.getMessageHandler().setChatHistory(connectionHandler.getMessageHandler().getDbHandler().getChatHistory(contact.getId()));
        connectionHandler.getMessageHandler().getDbHandler().close();

        // UI Stuff
        chatHistoryAdapter = new ChatHistoryAdapter(this, connectionHandler.getMessageHandler().getChatHistory());

        chatHistoryView = (ListView) findViewById(R.id.chat_list_view);
        chatHistoryView.setAdapter(chatHistoryAdapter);
        chatEdit = (EditText) findViewById(R.id.chat_edit_text1);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message newMessage = new Message();
                newMessage.setFrom(connectionHandler.getConnection().getUser().asEntityFullJidIfPossible());
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
                connectionHandler.getMessageHandler().getDbHandler().open();
                // create DB-Entry and add Item to chatHistoryList
                connectionHandler.getMessageHandler().getChatHistory().add(connectionHandler.getMessageHandler().getDbHandler().createMessage(newMessage.getFrom().toString(),
                        newMessage.getTo().toString(),
                        newMessage.getBody(),
                        c.getTime().toString(),
                        connectionHandler.getMessageHandler().getDbHandler().getContact(contact.getUsername()).getId()));
                // Close DB
                connectionHandler.getMessageHandler().getDbHandler().close();
                // Send message through XMPP
                    try {
                        if (connectionHandler.getConnection().isConnected() && connectionHandler.getConnection().isAuthenticated()) {
                            connectionHandler.getMessageHandler().getCurrentChat().send(newMessage);
                        }
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                // Send Push Notification
                Log.d("Push Notification: ","Sending Msg FROM: " + connectionHandler.getConnection().getUser().asEntityBareJidString() + "TO: " + newMessage.getTo());
                new PushMessageConnection(connectionHandler.getConnection().getUser().asEntityBareJidString(), newMessage.getTo().toString(), connectionHandler.getUserToken()).execute("");

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
     * Remove the Observer after Closing the ChatAcitivity
     * to prevent it from leaking memory.
     */
    @Override
    protected void onStop() {
        super.onStop();
        connectionHandler.getMessageHandler().deleteObservable(this);
    }

    /*
     * Called from Message Handler after incoming Message
     * was received. Calls to update the view
     */
    @Override
    public void updateMessageObservable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void updateConnectionObservable() {

    }
}
