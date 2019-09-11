package eu.ezlife.ezchat.ezchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;
import eu.ezlife.ezchat.ezchat.components.adapter.ContactListAdapter;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;
import eu.ezlife.ezchat.ezchat.fragments.ContactListDeleteFragment;
import eu.ezlife.ezchat.ezchat.fragments.ContactListRenameFragment;

/**
 * Created by ajo on 04.02.2017.
 * Activity which represents the Contact List View
 * Handles the Status and Contact List
 */
public class ContactListActivity extends BaseActivity implements ContactListDeleteFragment.ContactListDialogListener, ContactListRenameFragment.ContactListRenameListener {

    // Contact List UI
    private ListView contactList;
    private ArrayAdapter<ContactListEntry> contactListAdapter;

    // Fragments
    private ContactListDeleteFragment contactListDeleteDialog;
    private ContactListRenameFragment contactListRenameDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.disconnectConnection();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Modify Contact");
        getMenuInflater().inflate(R.menu.contact_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.item_view_profile:
                // TODO - implement ContactProfileFragment
                Log.d("ContextMenu", "for " + handler.contactList.get((int)info.id).getJid());
                contactListDeleteDialog.setCurrentContact(handler.contactList.get((int)info.id));
                return true;
            case R.id.item_rename_contact:
                // TODO - implement RenameContact
                Log.d("ContextMenu", "for " + handler.contactList.get((int)info.id).getJid());
                contactListRenameDialog.setCurrentContact(handler.contactList.get((int)info.id));
                contactListRenameDialog.show(getSupportFragmentManager(), "ContactListDialog");
                return true;
            case R.id.item_delete_contact:
                Log.d("ContextMenu", "for " + handler.contactList.get((int)info.id).getJid());
                contactListDeleteDialog.setCurrentContact(handler.contactList.get((int)info.id));
                contactListDeleteDialog.show(getSupportFragmentManager(), "ContactListDialog");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Add listener to Contact List
        contactListAdapter = new ContactListAdapter(this, handler.contactList);
        contactList = (ListView) findViewById(R.id.contact_list_view);
        contactList.setAdapter(contactListAdapter);

        // Register ContextMenu to ContactList
        registerForContextMenu(contactList);

        // Create additional Dialog for confirmations
        contactListDeleteDialog = new ContactListDeleteFragment();
        contactListRenameDialog = new ContactListRenameFragment();

        // Add onClickListener to all Items
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Load chatActivity
                Intent chatActivity = new Intent(getApplicationContext(), ChatActivity.class);
                Log.d("Contact", handler.getContactList().get(position).getJid().toString());
                chatActivity.putExtra("ContactListEntry", handler.contactList.get(position));
                chatActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(chatActivity);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.setCurrentChat(null);
    }

    /**
     * Called from XMPPHandler after incoming Message, connection change or
     * status change. Updates the view
     */
    @Override
    public void update(Observable o, Object arg) {
        Log.d("ContactList","Update Ui");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactListAdapter.notifyDataSetChanged();
            }
        });
    }

    // Dialog Callbacks
    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        Log.d("Dialog","DeletePressed");
        handler.removeContact(contactListDeleteDialog.getCurrentContact().getJid().toString());
    }

    @Override
    public void onDialogRenameClick(DialogFragment dialog, String name) {
        Log.d("Dialog","RenamePressed");
        handler.renameContact(contactListRenameDialog.getCurrentContact().getJid().toString(), name);
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        Log.d("Dialog","CancelPressed");
    }
}