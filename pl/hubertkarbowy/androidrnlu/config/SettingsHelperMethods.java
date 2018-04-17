package pl.hubertkarbowy.androidrnlu.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hubert on 17.04.18.
 */

public class SettingsHelperMethods {

    public static String readPrefs(Context ctxt, String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        return prefs.getString(key, defaultValue);
    }
}
