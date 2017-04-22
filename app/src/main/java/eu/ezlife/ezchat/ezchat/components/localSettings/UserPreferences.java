package eu.ezlife.ezchat.ezchat.components.localSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Holds the static user preferences
 */
// TODO - implement
public class SaveLoginPreferences {
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_PW = "password";
    static final String PREF_APP_ID = "appid";
    static final String PREF_FIREBASE_TOKEN = "fbToken";

    static SharedPreferences getSharedPreferences(Context ctx) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_APP_ID, "crWd-zEsn3I:APA91bG31FzgnPcwxSgTLeiXySl7oWq_x-neCEO_nXXEQaTovr8GnNxSiLo93MJYc4xpHN3khFkJQUDF5kFBU7BjpGsrBxwQ4shDxvGfNaH_Qwx2b08uk3zRxIy_MWzGiFSzjby36mUZ");
        editor.commit();
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setCredentials(Context ctx, String userName, String password) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_PW, password);
        editor.commit();
    }

    public static void setPrefFireBaseToken(Context ctx, String token) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_FIREBASE_TOKEN, token);
        editor.commit();
    }

    public static String getPrefFireBaseToken(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_FIREBASE_TOKEN, "");
    }

    public static String getPrefUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getPrefPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PW, "");
    }

    public static String getPrefAppId(Context ctx) {
        return  getSharedPreferences(ctx).getString(PREF_APP_ID, "");
    }


}
