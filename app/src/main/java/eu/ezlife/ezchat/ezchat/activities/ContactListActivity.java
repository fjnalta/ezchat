package eu.ezlife.ezchat.ezchat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.adapter.ContactListAdapter;
import eu.ezlife.ezchat.ezchat.components.server.XMPPConnection;
import eu.ezlife.ezchat.ezchat.components.database.DBDataSource;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ContactListActivity extends AppCompatActivity {

    private ListView myList;

    private Roster roster = Roster.getInstanceFor(XMPPConnection.getConnection());

    private List<ContactListEntry> contactList = new ArrayList<ContactListEntry>();
    private ArrayAdapter<ContactListEntry> contactListAdapter;
    private DBDataSource dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN",refreshedToken);

        // set DB-Handler
        dbHandler = new DBDataSource(this);
        // retrieve contact List from Server and update DB
        checkForDbUpdates();
        // create contact List on View
        createContactList();
        // Add listener to contact List
        setContactListAdapter();

        // Add listener for live updates
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {
                checkForDbUpdates();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactListAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void entriesUpdated(Collection<String> addresses) {
                checkForDbUpdates();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactListAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void entriesDeleted(Collection<String> addresses) {
                checkForDbUpdates();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactListAdapter.notifyDataSetChanged();
                    }
                });
            }
            // Ignored events public void entriesAdded(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                createContactList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
    
    private void checkForDbUpdates() {
        dbHandler.open();
        // check for new users
        if(dbHandler.getContacts().size() != roster.getEntryCount()) {
            for(RosterEntry entry : roster.getEntries()) {
                if(dbHandler.getContact(entry.getUser()) == null) {
                    dbHandler.createContact(entry.getUser(), R.mipmap.ic_launcher, entry.getName());
                }
            }
        }

        // check for avatar changes

        dbHandler.close();
    }

    private void createContactList() {
        try {
            if (!roster.isLoaded())
                roster.reloadAndWait();

            dbHandler.open();

            Collection<RosterEntry> rosterEntries = roster.getEntries();
            Presence presence;
            contactList.clear();

            for (RosterEntry entry : rosterEntries) {
                presence = roster.getPresence(entry.getUser());
                ContactListEntry currentEntry = new ContactListEntry(dbHandler.getContact(entry.getUser()),evaluateContactStatus(presence),presence.isAvailable(),false);
                currentEntry.setLastMessage(dbHandler.getLastMessage(entry.getUser()));
                contactList.add(currentEntry);
            }

            dbHandler.close();
        } catch (Exception e) {
            Log.w("app", e.toString());
        }
    }

    private void setContactListAdapter() {
        contactListAdapter = new ContactListAdapter(this, contactList);
        myList = (ListView) findViewById(R.id.contact_listView);
        myList.setAdapter(contactListAdapter);

        // Add onClickListener to all Items
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Load chatActivity
                Intent chatActivity = new Intent(getApplicationContext(), ChatActivity.class);

//                chatActivity.putExtra("EXTRA_USERNAME",contactList.get(position).getUsername());

                chatActivity.putExtra("ContactListEntry",contactList.get(position));
                chatActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(chatActivity);
            }
        });
    }

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
}