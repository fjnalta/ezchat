package eu.ezlife.ezchat.ezchat.components.firebaseServices;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ajo on 06.04.17.
 */
public class MyFireBaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // TODO - refresh token in userPreferences
    }
}