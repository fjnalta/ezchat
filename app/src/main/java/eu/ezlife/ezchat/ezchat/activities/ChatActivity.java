package eu.ezlife.ezchat.ezchat.activities;

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
import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;
import eu.ezlife.ezchat.ezchat.components.adapter.ChatHistoryAdapter;
import eu.ezlife.ezchat.ezchat.components.database.DBDataSource;
import eu.ezlife.ezchat.ezchat.components.restServices.PushMessageConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 04.02.2017.
 * Activity which represents the Chat View
 * Handles outgoing XMPP and Push messages
 */
public class ChatActivity extends BaseActivity {

    // Serializable intent
    private ContactListEntry contact;
    // UI Stuff
    private ImageView sendButton;
    private EditText chatEdit;
    private ListView chatHistoryView;
    // Chat history
//    private ArrayAdapter<ChatHistoryEntry> chatHistoryAdapter;
    // DatabaseHandler
    private DBDataSource dbHandler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHandler = new DBDataSource(this);

        // Get Contact Information from Intent
        if (getIntent().getSerializableExtra("ContactListEntry") != null) {
            this.contact = (ContactListEntry) getIntent().getSerializableExtra("ContactListEntry");
        }

        dbHandler.open();

        // Set or create current Chat
        try {
            Jid jid = JidCreate.from(contact.getJid());
            handler.setCurrentChat(handler.getChatManager().chatWith(jid.asEntityBareJidIfPossible()));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        // Set the current Chat history

//        handler.setChatHistory(dbHandler.getChatHistory(contact.getJid()));
        dbHandler.close();

        // UI Stuff
/*        chatHistoryAdapter = new ChatHistoryAdapter(this, handler.getChatHistory());
        chatHistoryView = (ListView) findViewById(R.id.chat_list_view);
        chatHistoryView.setAdapter(chatHistoryAdapter);
        chatHistoryView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatHistoryView.setStackFromBottom(true);*/

        chatEdit = (EditText) findViewById(R.id.chat_edit_text1);
        sendButton = (ImageView) findViewById(R.id.enter_chat1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message newMessage = new Message();
                newMessage.setFrom(handler.connection.getUser().asEntityFullJidIfPossible());
                Jid myUsername = null;
                try {
                    myUsername = JidCreate.from(contact.getJid());
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                newMessage.setTo(myUsername);
                newMessage.setBody(chatEdit.getText().toString());
                // Create Custom Time Format for DB Sorting
                Calendar c = Calendar.getInstance();
                // Open DB and save Message
/*                dbHandler.open();
                // create DB-Entry and add Item to chatHistoryList
                handler.getChatHistory().add(dbHandler.createMessage(newMessage.getFrom().toString(),
                        newMessage.getTo().toString(),
                        newMessage.getBody(),
                        c.getTime().toString(),
                        dbHandler.getContact(contact.getJid()).getId()));
                // Close DB
                dbHandler.close();*/

                // Set last Message
                for (ContactListEntry entry : handler.getContactList()) {
                    if (entry.getJid().equals(newMessage.getTo().toString())) {
                        entry.setLastMessage(newMessage.getBody());
                    }
                }

                // Send message through XMPP
                try {
                    if (handler.connection.isConnected() && handler.connection.isAuthenticated()) {
                        handler.getCurrentChat().send(newMessage);
                    }
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // TODO - use AsyncTask stuff
                // Send Push Notification
                Log.d("Push Notification: ", "Sending Msg FROM: " + handler.connection.getUser().asEntityBareJidString() + "TO: " + newMessage.getTo());
//                new PushMessageConnection(handler.connection.getUser().asEntityBareJidString(), newMessage.getTo().toString(), prefs.getPrefFireBaseToken(), prefs.getPrefAppId()).execute("");

                // Reset TextBox
                chatEdit.setText("");
                // notify about the changes
/*                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatHistoryAdapter.notifyDataSetChanged();
                    }
                });*/
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        toolbar.setTitle("Chat with - " + this.contact.getNickName());
        toolbar.setSubtitle(this.contact.getJid());
        setSupportActionBar(toolbar);
    }

    /*
     * Called from XMPPHandler after incoming Message
     * was received. Updates the view.
     */
    @Override
    public void update(Observable o, Object arg) {
/*        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistoryAdapter.notifyDataSetChanged();
            }
        });*/
    }
}
