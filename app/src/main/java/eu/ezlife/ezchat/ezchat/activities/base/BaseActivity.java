package eu.ezlife.ezchat.ezchat.activities.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Observer;

import eu.ezlife.ezchat.ezchat.activities.LoginActivity;
import eu.ezlife.ezchat.ezchat.components.localSettings.UserPreferences;
import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService;

/**
 * Created by ajo on 28.04.17.
 * This class handles tracking when the app is going to
 * background or foreground.
 */
public abstract class BaseActivity extends AppCompatActivity implements XMPPService, Observer {

    // track Application status
    protected static final String TAG = BaseActivity.class.getName();
    public static boolean isAppWentToBg = false;
    public static boolean isWindowFocused = false;
    public static boolean isBackPressed = false;

    // User Preferences
    public UserPreferences prefs = null;

    @Override
    protected void onStart() {

        handler.setAndroidContext(this);
        handler.addObserver(this);

        applicationWillEnterForeground();
        super.onStart();
    }

    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            Toast.makeText(getApplicationContext(), "Connecting",
                    Toast.LENGTH_SHORT).show();
            handler.buildConnection(prefs.getPrefUserName(),prefs.getPrefPassword());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.prefs = new UserPreferences(this);
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
        } else {
            isBackPressed = true;
        }
        Log.d(TAG, "onBackPressed " + isBackPressed + "" + this.getLocalClassName());
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

}