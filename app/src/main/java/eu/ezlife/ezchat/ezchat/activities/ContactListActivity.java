package eu.ezlife.ezchat.ezchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;
import eu.ezlife.ezchat.ezchat.components.adapter.ContactListAdapter;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 04.02.2017.
 * Activity which represents the Contact List View
 * Handles the Status and Contact List
 */
public class ContactListActivity extends BaseActivity {

    // Contact List UI
    private ListView myList;
    private ArrayAdapter<ContactListEntry> contactListAdapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.disconnectConnection();
        handler.deleteObserver(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.setCurrentChat(null);
        contactListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Add listener to contact List
        contactListAdapter = new ContactListAdapter(this, handler.getContactList());
        myList = (ListView) findViewById(R.id.contact_listView);
        myList.setAdapter(contactListAdapter);

        // Add onClickListener to all Items
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Load chatActivity
                Intent chatActivity = new Intent(getApplicationContext(), ChatActivity.class);
                chatActivity.putExtra("ContactListEntry", handler.getContactList().get(position));
                chatActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(chatActivity);
            }
        });

        // TODO - implement Long click -> delete contact, rename contact
        myList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    /**
     * Called from XMPPHandler after incoming Message, connection change or
     * status change. Updates the view
     */
    @Override
    public void update(Observable o, Object arg) {
        Log.d("ContactList","UpdateRoster");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    contactListAdapter.notifyDataSetChanged();
                }
            });
    }
}
