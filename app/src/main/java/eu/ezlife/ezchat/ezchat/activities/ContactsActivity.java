package eu.ezlife.ezchat.ezchat.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;

public class ContactsActivity extends BaseActivity {

    protected static final String TAG = "ContactsActivity";
    private Button addContactButton;
    private EditText username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        username = (EditText) findViewById(R.id.input_add_contact);

        // Create Add User Button
        addContactButton = (Button) findViewById(R.id.btn_add_contact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.addContact(username.getText().toString());
                username.setText("");
                finish();
            }
        });

        // TODO - List of pending requests
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set Toolbar
        toolbar.setTitle("Contacts");
        setSupportActionBar(toolbar);
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
