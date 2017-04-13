package eu.ezlife.ezchat.ezchat.components.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import eu.ezlife.ezchat.ezchat.components.server.TokenRegistrationConnection;
import eu.ezlife.ezchat.ezchat.components.server.XMPPConnection;

/**
 * Created by ajo on 06.04.17.
 */

public class MyIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirebaseCloudMessaging", "Refreshed token: " + refreshedToken);
    }
}
