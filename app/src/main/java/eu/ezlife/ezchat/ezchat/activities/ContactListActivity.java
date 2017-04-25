package eu.ezlife.ezchat.ezchat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Collection;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.adapter.ContactListAdapter;
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService;
import eu.ezlife.ezchat.ezchat.components.restServices.TokenRegistrationConnection;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 04.02.2017.
 * Activity which represents the Contact List View
 * Handles the Status and Contact List
 */
public class ContactListActivity extends AppCompatActivity implements XMPPService {

    // Contact List UI
    private ListView myList;
    private ArrayAdapter<ContactListEntry> contactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Register on PushMessage Listener and Remove the current chat
        connectionHandler.registerObservable(this, getApplicationContext());

        connectionHandler.getMessageHandler().registerObservable(this, getApplicationContext());
        connectionHandler.getMessageHandler().setCurrentChat(null);

        // TODO - move this
        // Register Created Token after Login
        new TokenRegistrationConnection(connectionHandler.getUserToken(), connectionHandler.getConnection().getUser().asEntityBareJidString()).execute("");

        // retrieve contact List from Server and update DB
        checkForDbUpdates();
        // Add listener to contact List
        setContactListAdapter();
    }
    
    private void checkForDbUpdates() {
        connectionHandler.getMessageHandler().getDbHandler().open();
        // check for new users
        if(connectionHandler.getMessageHandler().getDbHandler().getContacts().size() != connectionHandler.getMessageHandler().getRoster().getEntryCount() && connectionHandler.getMessageHandler().getRoster().getEntryCount() != 0) {
            for(RosterEntry entry : connectionHandler.getMessageHandler().getRoster().getEntries()) {
                if(connectionHandler.getMessageHandler().getDbHandler().getContact(entry.getUser()) == null) {
                    connectionHandler.getMessageHandler().getDbHandler().createContact(entry.getUser(), R.mipmap.ic_launcher, entry.getName());
                }
            }
        }

        // check for avatar changes

        connectionHandler.getMessageHandler().getDbHandler().close();
    }

    private void createContactList() {
        try {
            if (!connectionHandler.getMessageHandler().getRoster().isLoaded())
                connectionHandler.getMessageHandler().getRoster().reloadAndWait();

            connectionHandler.getMessageHandler().getDbHandler().open();

            Collection<RosterEntry> rosterEntries = connectionHandler.getMessageHandler().getRoster().getEntries();
            Presence presence;
            connectionHandler.getMessageHandler().getContactList().clear();

            for (RosterEntry entry : rosterEntries) {
                presence = connectionHandler.getMessageHandler().getRoster().getPresence(entry.getJid());
                ContactListEntry currentEntry = new ContactListEntry(connectionHandler.getMessageHandler().getDbHandler().getContact(entry.getUser()),evaluateContactStatus(presence),presence.isAvailable(),false);
                currentEntry.setLastMessage(connectionHandler.getMessageHandler().getDbHandler().getLastMessage(entry.getUser()));
                connectionHandler.getMessageHandler().getContactList().add(currentEntry);
            }

            connectionHandler.getMessageHandler().getDbHandler().close();
            contactListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContactListAdapter() {
        contactListAdapter = new ContactListAdapter(this, connectionHandler.getMessageHandler().getContactList());
        myList = (ListView) findViewById(R.id.contact_listView);
        myList.setAdapter(contactListAdapter);

        // Add onClickListener to all Items
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Load chatActivity
                Intent chatActivity = new Intent(getApplicationContext(), ChatActivity.class);
                chatActivity.putExtra("ContactListEntry", connectionHandler.getMessageHandler().getContactList().get(position));
                chatActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(chatActivity);
            }
        });
    }

    /**
     * Helper method to create visual status icons from
     * the user presence
     * @param presence Presence to evaluate
     */
    private int evaluateContactStatus(Presence presence){
        int stat = R.drawable.icon_offline;
        if(presence.isAvailable()) {
            if(presence.getMode().name().equals("available")) {
                stat = R.drawable.icon_online;
            }
            if(presence.getMode().name().equals("away")) {
                stat = R.drawable.icon_away;
            }
            if(presence.getMode().name().equals("dnd")) {
                stat = R.drawable.icon_dnd;
            }
        } else {
            stat = R.drawable.icon_offline;
        }
        return stat;
    }

    /**
     * Called from Message Handler after incoming Message
     * was received. Calls to update the view
     */
    @Override
    public void updateMessageObservable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createContactList();
                contactListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void updateConnectionObservable() {
        if(connectionHandler.getMessageHandler().isRosterLoaded()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createContactList();
                    contactListAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
