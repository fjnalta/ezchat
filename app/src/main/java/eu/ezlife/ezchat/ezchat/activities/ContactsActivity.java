package eu.ezlife.ezchat.ezchat.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jxmpp.jid.Jid;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;
import eu.ezlife.ezchat.ezchat.components.adapter.NewContactAdapter;

public class ContactsActivity extends BaseActivity {

    protected static final String TAG = "ContactsActivity";
    private ListView newContactList;
    private Button addContactButton;
    private EditText username;
    private ArrayAdapter<Jid> newContactAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Add listener to Contact List
//        newContactAdapter = new NewContactAdapter(this, handler.getNewSubs());
        newContactList = (ListView) findViewById(R.id.new_contact_list_view);
//        newContactList.setAdapter(newContactAdapter);

        username = (EditText) findViewById(R.id.input_add_contact);

        addContactButton = (Button) findViewById(R.id.btn_add_contact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.addContact(username.getText().toString());
                username.setText("");
            }
        });

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
