package pl.hubertkarbowy.androidrnlu.nativeapps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by hubert on 17.04.18.
 */

public class NativeApps  {

    public static String resolvePackageName(String appName) {

        return null;
    }

    public static void runCalendar(Handler handler) {

    }

//    public static Intent getSettingsIntent() {
//        Intent intent = new Intent(Settings.ACTION_SETTINGS);
//        return intent;
//    }

    public static Intent fetchIntent(String nlRecognizedIntent, Map<String, String> slots) {

        Intent foundAction = null;
        if (nlRecognizedIntent.equals("AppLaunch")) {
            String pkgName = slots.get("AppName");
            if (pkgName.equals("Settings")) foundAction = new Intent(Settings.ACTION_SETTINGS);
            else if (pkgName.equals("Settings _Net")) foundAction = new Intent(Settings.ACTION_WIFI_SETTINGS);
            else if (pkgName.equals("Alarms _ShowAll")) foundAction = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            // nie ma else!
        }
        else if (nlRecognizedIntent.equals("NoOp")) {
            Log.d("NLUMODULE", "Client operation not supported");
        }

        return foundAction;
    }

}
