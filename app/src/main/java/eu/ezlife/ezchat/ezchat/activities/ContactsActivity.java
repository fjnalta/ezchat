package eu.ezlife.ezchat.ezchat.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jxmpp.jid.Jid;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;
import eu.ezlife.ezchat.ezchat.components.adapter.NewContactAdapter;

public class ContactsActivity extends BaseActivity {

    protected static final String TAG = "ContactsActivity";
    private ListView newContactList;
    private ArrayAdapter<Jid> newContactAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Add listener to Contact List
        newContactAdapter = new NewContactAdapter(this, handler.getNewSubs());
        newContactList = (ListView) findViewById(R.id.new_contact_list_view);
        newContactList.setAdapter(newContactAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        toolbar.setTitle("Contacts");
        setSupportActionBar(toolbar);
    }


    @Override
    public void update(Observable o, Object arg) {
    }
}
