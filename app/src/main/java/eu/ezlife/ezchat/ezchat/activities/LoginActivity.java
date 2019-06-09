package eu.ezlife.ezchat.ezchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Observable;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.base.BaseActivity;
import eu.ezlife.ezchat.ezchat.data.ObserverObject;

public class LoginActivity extends BaseActivity {

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;

    private ProgressDialog progressDialog;

    // TODO - implement register Activity

    @Override
    protected void onStart() {
        super.onStart();
        handler.setCurrentChat(null);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.input_username);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                // Create Connection
                prefs.setCredentials(usernameText.getText().toString(), passwordText.getText().toString());
                handler.buildConnection(prefs.getPrefUserName(), prefs.getPrefPassword());
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg != null) {
            ObserverObject response = (ObserverObject) arg;
            if (response.getText().equals("authenticated")) {
                // Connection successful
                Intent contactListActivity = new Intent(getApplicationContext(), ContactListActivity.class);
                contactListActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(contactListActivity);

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
            }
        }
    }
}