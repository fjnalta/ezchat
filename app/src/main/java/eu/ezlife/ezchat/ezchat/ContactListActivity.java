package eu.ezlife.ezchat.ezchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;

import eu.ezlife.ezchat.ezchat.components.myXMPPConnection;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ContactListActivity extends AppCompatActivity {

    private ListView myList;
    private ArrayAdapter<ContactListEntry> contactsAdapter;
    private Roster roster = Roster.getInstanceFor(myXMPPConnection.getConnection());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Retrieve the contactList from Server
        myXMPPConnection.updateContactList();
        // create contact List on View
        createContactList();
        // Add listener for contact status
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {}
            @Override
            public void entriesUpdated(Collection<String> addresses) {}
            @Override
            public void entriesDeleted(Collection<String> addresses) {}
            // Ignored events public void entriesAdded(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                Log.d("ContactListActivity",presence.getFrom());
                myXMPPConnection.updateContactList();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void createContactList() {
        contactsAdapter = new ArrayAdapter<ContactListEntry>(getApplicationContext(),android.R.layout.simple_list_item_1,myXMPPConnection.getContactList());
        myList = (ListView) findViewById(R.id.contact_list);
        myList.setAdapter(contactsAdapter);
        // Add onClickListener to all Items
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Load chatActivity
                Intent chatActivity = new Intent(getApplicationContext(), eu.ezlife.ezchat.ezchat.ChatActivity.class);
                chatActivity.putExtra("EXTRA_USERNAME",myXMPPConnection.getContactList().get(position).getUsername());
                getApplicationContext().startActivity(chatActivity);
            }
        });
    }
}