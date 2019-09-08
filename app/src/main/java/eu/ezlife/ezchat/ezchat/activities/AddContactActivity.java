package eu.ezlife.ezchat.ezchat.activities;

import android.os.Bundle;
import android.util.Log;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;

public class AddContactActivity extends BaseActivity {

    protected static final String TAG = "AddContactActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
    }

    @Override
    public void onStart() {
        super.onStart();

        toolbar.setTitle("Add Contact");
        setSupportActionBar(toolbar);
    }


    @Override
    public void update(Observable o, Object arg) {
    }
}
