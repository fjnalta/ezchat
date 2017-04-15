package eu.ezlife.ezchat.ezchat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Collection;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.adapter.ContactListAdapter;
import eu.ezlife.ezchat.ezchat.components.listener.PushMessageService;
import eu.ezlife.ezchat.ezchat.components.listener.XMPPConnectionService;
import eu.ezlife.ezchat.ezchat.components.restServices.TokenRegistrationConnection;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;


/**
 * Created by ajo on 04.02.2017.
 * Activity which represents the Contact List View
 * Handles the Status and Contact List
 */
public class ContactListActivity extends AppCompatActivity implements PushMessageService, XMPPConnectionService {

    // Contact List UI
    private ListView myList;
    private ArrayAdapter<ContactListEntry> contactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Register on PushMessage Listener and Remove the current chat
        handler.registerObservable(this, getApplicationContext());
        handler.setCurrentChat(null);

        // TODO - move this
        // Register Created Token after Login
        new TokenRegistrationConnection(connectionHandler.getUserToken(), connectionHandler.getConnection().getUser().asEntityBareJidString()).execute("");

        // retrieve contact List from Server and update DB
        checkForDbUpdates();
        // create contact List on View
        createContactList();
        // Add listener to contact List
        setContactListAdapter();
    }
    
    private void checkForDbUpdates() {
        handler.getDbHandler().open();
        // check for new users
        if(handler.getDbHandler().getContacts().size() != handler.getRoster().getEntryCount() && handler.getRoster().getEntryCount() != 0) {
            for(RosterEntry entry : handler.getRoster().getEntries()) {
                if(handler.getDbHandler().getContact(entry.getUser()) == null) {
                    handler.getDbHandler().createContact(entry.getUser(), R.mipmap.ic_launcher, entry.getName());
                }
            }
        }

        // check for avatar changes

        handler.getDbHandler().close();
    }

    private void createContactList() {
        try {
            if (!handler.getRoster().isLoaded())
                handler.getRoster().reloadAndWait();

            handler.getDbHandler().open();

            Collection<RosterEntry> rosterEntries = handler.getRoster().getEntries();
            Presence presence;
            handler.getContactList().clear();

            for (RosterEntry entry : rosterEntries) {
                presence = handler.getRoster().getPresence(entry.getJid());
                ContactListEntry currentEntry = new ContactListEntry(handler.getDbHandler().getContact(entry.getUser()),evaluateContactStatus(presence),presence.isAvailable(),false);
                currentEntry.setLastMessage(handler.getDbHandler().getLastMessage(entry.getUser()));
                handler.getContactList().add(currentEntry);
            }

            handler.getDbHandler().close();
            contactListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContactListAdapter() {
        contactListAdapter = new ContactListAdapter(this, handler.getContactList());
        myList = (ListView) findViewById(R.id.contact_listView);
        myList.setAdapter(contactListAdapter);

        // Add onClickListener to all Items
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Load chatActivity
                Intent chatActivity = new Intent(getApplicationContext(), ChatActivity.class);
                chatActivity.putExtra("ContactListEntry",handler.getContactList().get(position));
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
    public void updateObservable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createContactList();
                contactListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void notifyConnectionInterface() {

    }
}