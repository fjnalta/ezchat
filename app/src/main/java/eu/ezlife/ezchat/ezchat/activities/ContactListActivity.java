package eu.ezlife.ezchat.ezchat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    protected void onResume() {
        super.onResume();
        connectionHandler.getMessageHandler().registerObservable(this, getApplicationContext());
        connectionHandler.getMessageHandler().setCurrentChat(null);

        contactListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        connectionHandler.registerObservable(this, getApplicationContext());

        // Add listener to contact List
        setContactListAdapter();
    }

    private void setContactListAdapter() {

        Log.d("ContactListAdapter","notified");

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
     * Called from Message Handler after incoming Message
     * was received. Calls to update the view
     */
    @Override
    public void updateMessageObservable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void updateConnectionObservable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionHandler.getMessageHandler().deleteObservable(this);
    }
}
