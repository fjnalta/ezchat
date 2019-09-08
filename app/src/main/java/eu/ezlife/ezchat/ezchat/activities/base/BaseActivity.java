package eu.ezlife.ezchat.ezchat.activities.base;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.minidns.dnsserverlookup.android21.AndroidUsingLinkProperties;

import java.util.Observer;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.activities.ContactsActivity;
import eu.ezlife.ezchat.ezchat.activities.ContactListActivity;
import eu.ezlife.ezchat.ezchat.activities.LoginActivity;
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 28.04.17.
 * This class handles tracking when the app is going to
 * background or foreground.
 */
public abstract class BaseActivity extends AppCompatActivity implements XMPPService, Observer {

    // Track Application status
    protected static final String TAG = "BaseActivity";
    public static boolean isAppWentToBg = false;
    public static boolean isWindowFocused = false;
    public static boolean isBackPressed = false;

    public Toolbar toolbar;
    public SharedPreferences sharedPref;

    @Override
    protected void onStart() {
        super.onStart();

        handler.setAndroidContext(this);
        handler.addObserver(this);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load Shared Preferences
        sharedPref = getSharedPreferences("ezchat", MODE_PRIVATE);

        // Handle XMPP Connection
        checkForSavedLogin();

        // Handle Application State
        applicationWillEnterForeground();

        createNotificationChannel();
    }

    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUsingLinkProperties.setup(this.getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove observer
        handler.deleteObserver(this);
        // handle XMPP disconnect
        applicationDidEnterBackground();
    }

    private void applicationDidEnterBackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            Toast.makeText(getApplicationContext(), "Disconnecting",
                    Toast.LENGTH_SHORT).show();

            handler.disconnectConnection();
        }
    }

    @Override
    public void onBackPressed() {
        if (this instanceof LoginActivity) {
        }
        if (this instanceof ContactListActivity) {
            onStop();
            moveTaskToBack(true);
        }
        else {
            isBackPressed = true;
        }
        Log.d(TAG, "onBackPressed " + isBackPressed + "" + this.getLocalClassName());
        handler.deleteObserver(this);
        super.onBackPressed();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isWindowFocused = hasFocus;
        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }
        super.onWindowFocusChanged(hasFocus);
    }

    // Handle Top Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_add_contact:
                Log.d(TAG, "Contacts");
                if (!(this instanceof ContactsActivity)) {
                    Intent addContactActivity = new Intent(getApplicationContext(), ContactsActivity.class);
                    addContactActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(addContactActivity);
                    break;
                }
                break;

            case R.id.menu_settings:
                // TODO - Show Settings Activity
                Log.d(TAG, "Settings");
                break;

            case R.id.menu_logout:
                // delete User Prefs and return to LoginActivity
                Log.d(TAG, "Logout");

                Editor editor = sharedPref.edit();
                editor.putString("PREF_USER_NAME", "");
                editor.putString("PREF_USER_PW", "");
                editor.apply();

                if (handler.connection != null) {
                    handler.disconnectConnection();
                }

                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(loginActivity);
                break;

            default:
                break;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks the user Preferences for saved login
     */
    private void checkForSavedLogin() {
        if (!(sharedPref.getString("PREF_USER_NAME","").equals("")) && !(sharedPref.getString("PREF_USER_PW","").equals(""))) {
            if (handler.connected == false || handler.loggedIn == false) {
                // Create Connection
                handler.buildConnection(sharedPref.getString("PREF_USER_NAME",""), sharedPref.getString("PREF_USER_PW",""));
            }
        } else {
            // Back to Login Activity
            if(!(this instanceof LoginActivity)) {
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getApplicationContext().startActivity(loginActivity);
            }
        }
    }

    /**
     * Fucking Notifications
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ezchat", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}