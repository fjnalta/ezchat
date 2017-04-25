package eu.ezlife.ezchat.ezchat.components.localSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import de.measite.minidns.record.A;

/**
 * Holds the static user preferences
 */
public class UserPreferences {

    // UserPrefs
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_PW = "password";
    static final String PREF_APP_ID = "appid";
    static final String PREF_FIREBASE_TOKEN = "fbToken";

    // Google AppId
    static final String APP_ID = "crWd-zEsn3I:APA91bG31FzgnPcwxSgTLeiXySl7oWq_x-neCEO_nXXEQaTovr8GnNxSiLo93MJYc4xpHN3khFkJQUDF5kFBU7BjpGsrBxwQ4shDxvGfNaH_Qwx2b08uk3zRxIy_MWzGiFSzjby36mUZ";

    // AppContext
    private Context context;

    public UserPreferences(Context ctx) {
        this.context = ctx;
        setPrefAppId();
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void setCredentials(String userName, String password) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_PW, password);
        editor.commit();
    }

    public void setPrefFireBaseToken(String token) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_FIREBASE_TOKEN, token);
        editor.commit();
    }

    public String getPrefFireBaseToken() {
        return getSharedPreferences().getString(PREF_FIREBASE_TOKEN, "");
    }

    public String getPrefUserName() {
        return getSharedPreferences().getString(PREF_USER_NAME, "");
    }

    public String getPrefPassword() {
        return getSharedPreferences().getString(PREF_USER_PW, "");
    }

    public void setPrefAppId() {
        Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_APP_ID, APP_ID);
        editor.commit();
    }

    public String getPrefAppId() {
        return  getSharedPreferences().getString(PREF_APP_ID, "");
    }
}
