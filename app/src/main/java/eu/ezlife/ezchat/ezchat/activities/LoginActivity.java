package eu.ezlife.ezchat.ezchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.localSettings.UserPreferences;
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService;

public class LoginActivity extends AppCompatActivity implements XMPPService {

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectionHandler.registerObservable(this, getApplicationContext());

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
                // Set user credentials
                connectionHandler.setCredentials(usernameText.getText().toString(), passwordText.getText().toString());
                // Create Connection
                connectionHandler.buildConnection();
                connectionHandler.connectConnection();
            }
        });

        checkForSavedLogin();
    }

    private void checkForSavedLogin() {
        UserPreferences prefs = new UserPreferences(getApplicationContext());
        if(!prefs.getPrefUserName().equals("") && !prefs.getPrefPassword().equals("")) {
            progressDialog.show();
            connectionHandler.setCredentials(prefs.getPrefUserName(), prefs.getPrefPassword());
            // Create Connection
            connectionHandler.buildConnection();
            connectionHandler.connectConnection();
        }
    }

    @Override
    public void updateMessageObservable() {
        // No need to handle this in login activity
    }

    @Override
    public void updateConnectionObservable() {
        if(connectionHandler.getConnection() != null) {
            // Connection successful
            if(connectionHandler.isLoggedIn() && connectionHandler.isConnected()) {
                Intent contactListActivity = new Intent(getApplicationContext(), ContactListActivity.class);
                contactListActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(contactListActivity);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();
                    }
                });
            }

            // Login failed
            if(connectionHandler.isConnected() && !connectionHandler.isLoggedIn() || !connectionHandler.isConnected() && !connectionHandler.isLoggedIn()) {
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

    /*
 * Remove the Observer after Closing the LoginActivity
 * to prevent it from leaking memory.
 */
    @Override
    protected void onStop() {
        super.onStop();
        connectionHandler.deleteObservable(this);
    }
}