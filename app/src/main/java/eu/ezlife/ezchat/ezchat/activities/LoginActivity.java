package eu.ezlife.ezchat.ezchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPConnectionHandler;
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPConnectionService;

public class LoginActivity extends AppCompatActivity implements XMPPConnectionService {

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectionHandler.registerObservable(this);

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
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                // Create Connection
                // TODO - make async call while login to validate status
                Intent i = new Intent(getBaseContext(),XMPPConnectionHandler.class);
                i.putExtra("user",username);
                i.putExtra("pwd",password);
                startService(i);

                connectionHandler.connectConnection();
            }
        });
    }

    @Override
    public void notifyConnectionInterface() {
        Log.d("LoginActivity", "Update Called");
        if(connectionHandler.getConnection() != null) {
            if(connectionHandler.getConnection().isAuthenticated()) {
                Intent contactListActivity = new Intent(getApplicationContext(), ContactListActivity.class);
                contactListActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(contactListActivity);
                progressDialog.hide();
            }
        }
    }
}