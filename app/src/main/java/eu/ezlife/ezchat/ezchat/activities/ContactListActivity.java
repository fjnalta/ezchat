package eu.ezlife.ezchat.ezchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;
import eu.ezlife.ezchat.ezchat.components.adapter.ContactListAdapter;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;
import eu.ezlife.ezchat.ezchat.data.ObserverObject;

/**
 * Created by ajo on 04.02.2017.
 * Activity which represents the Contact List View
 * Handles the Status and Contact List
 */
public class ContactListActivity extends BaseActivity {

    // Contact List UI
    private ListView myList;
    private ArrayAdapter<ContactListEntry> contactListAdapter;
    private ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.disconnectConnection();
        handler.deleteObserver(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForSavedLogin();
        handler.setCurrentChat(null);
        contactListAdapter.notifyDataSetChanged();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        if(arg != null) {
            ObserverObject response = (ObserverObject) arg;
            if (response.getText().equals("authenticated")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();
                    }
                });
            } else {
                // Connection failed
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
                // Back to login activity
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(loginActivity);
            }
        }

        Log.d("ContactList","UpdateRoster");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    contactListAdapter.notifyDataSetChanged();
                }
            });
    }

    /**
     * Checks the user Preferences for saved login
     */
    private void checkForSavedLogin() {
        progressDialog = new ProgressDialog(ContactListActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        if (!prefs.getPrefUserName().equals("") && !prefs.getPrefPassword().equals("")) {
            if (handler.connection == null) {
                progressDialog.show();
                // Create Connection
                handler.buildConnection(prefs.getPrefUserName(), prefs.getPrefPassword());
            }
        }
    }
}
