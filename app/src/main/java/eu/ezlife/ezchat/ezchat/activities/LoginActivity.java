package eu.ezlife.ezchat.ezchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.localSettings.UserPreferences;
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService;
import eu.ezlife.ezchat.ezchat.data.ObserverObject;

public class LoginActivity extends AppCompatActivity implements XMPPService, Observer {

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;

    private ProgressDialog progressDialog;

    // UserPreferences
    private UserPreferences prefs = null;

    @Override
    protected void onStop() {
        super.onStop();
        handler.deleteObserver(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.setAndroidContext(this);
        handler.addObserver(this);
        handler.setCurrentChat(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = new UserPreferences(this);

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

        checkForSavedLogin();
    }

    /**
     * Checks the user Preferences for saved logins
     */
    private void checkForSavedLogin() {
        if (!prefs.getPrefUserName().equals("") && !prefs.getPrefPassword().equals("")) {
            progressDialog.show();
            // Create Connection
            handler.buildConnection(prefs.getPrefUserName(), prefs.getPrefPassword());
        }
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
                // TODO - add loginFailed status code
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