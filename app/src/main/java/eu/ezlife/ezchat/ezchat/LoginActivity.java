package eu.ezlife.ezchat.ezchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import eu.ezlife.ezchat.ezchat.components.SaveLoginPreference;
import eu.ezlife.ezchat.ezchat.components.myXMPPConnection;

public class LoginActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);

        usernameText = (EditText) findViewById(R.id.input_username);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stop if login validation fails
                if (!localValidation()) {
                    onLoginFailed();
                    return;
                }

                loginButton.setEnabled(false);

                // show process screen
                progressDialog.show();

                final String username = usernameText.getText().toString();
                final String password = passwordText.getText().toString();

                // create connection
                myXMPPConnection con = new myXMPPConnection(username, password);
                con.execute("");

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (myXMPPConnection.getConnection().isAuthenticated()) {
                                    Log.d("LoginActivity","success");
                                    SaveLoginPreference.setCredentials(getApplicationContext(),username,password);
                                    onLoginSuccess();
                                } else {
                                    Log.d("LoginActivity","failed");
                                    onLoginFailed();
                                }
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        });

        // if user has valid login
        if (SaveLoginPreference.getUserName(getApplicationContext()).length() != 0 || SaveLoginPreference.getPassword(getApplicationContext()).length() != 0) {
            progressDialog.show();
            myXMPPConnection con = new myXMPPConnection(SaveLoginPreference.getUserName(getApplicationContext()), SaveLoginPreference.getPassword(getApplicationContext()));
            con.execute("");

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if (myXMPPConnection.getConnection().isAuthenticated()) {
                                Log.d("LoginActivity","success");
                                onLoginSuccess();
                            } else {
                                Log.d("LoginActivity","failed");
                                onLoginFailed();
                            }
                        }
                    }, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);

        Intent contactListActivity = new Intent(getApplicationContext(), eu.ezlife.ezchat.ezchat.ContactListActivity.class);
        getApplicationContext().startActivity(contactListActivity);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean localValidation() {
        boolean valid = true;

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty()) {
            usernameText.setError("enter a valid username");
            valid = false;
        } else {
            usernameText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("wrong password");
            valid = false;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }
}