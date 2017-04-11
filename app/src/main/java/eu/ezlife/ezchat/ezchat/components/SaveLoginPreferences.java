package eu.ezlife.ezchat.ezchat.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by ajo on 04.02.2017.
 */

public class SaveLoginPreferences {
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_PW = "password";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setCredentials(Context ctx, String userName, String password) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_PW, password);
        editor.commit();
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PW, "");
    }


}
