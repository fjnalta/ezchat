package eu.ezlife.ezchat.ezchat.components.firebaseServices;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import eu.ezlife.ezchat.ezchat.components.localSettings.UserPreferences;

/**
 * Created by ajo on 06.04.17.
 */
public class MyFireBaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        UserPreferences prefs = new UserPreferences(getApplicationContext());
        prefs.setPrefFireBaseToken(refreshedToken);

        // TODO - update token at REST Server
    }
}