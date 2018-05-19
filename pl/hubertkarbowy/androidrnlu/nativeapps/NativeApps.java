package pl.hubertkarbowy.androidrnlu.nativeapps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

import pl.hubertkarbowy.androidrnlu.ElicitationException;

/**
 * Created by hubert on 17.04.18.
 */

public class NativeApps  {

    public static String resolvePackageName(String appName) {

        return null;
    }

    public static Intent getAppCategoryIntent(String ia) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(ia);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent fetchIntent(String nlRecognizedIntent, Map<String, String> slots) throws ElicitationException {

        Intent foundAction = null;
        if (nlRecognizedIntent.equals("AppLaunch")) {
            String pkgName = slots.get("AppName");
            if (pkgName.equals("Settings")) foundAction = new Intent(Settings.ACTION_SETTINGS);
            else if (pkgName.equals("Settings _Net") || pkgName.equals("Network Settings")) foundAction = new Intent(Settings.ACTION_WIFI_SETTINGS);
            else if (pkgName.equals("Alarms _ShowAll") || pkgName.equals("Alarms")) foundAction = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            else if (pkgName.equals("Alarms _ShowAll") || pkgName.equals("Alarms")) foundAction = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            else if (pkgName.equals("Browser") || pkgName.equals("WebBrowser")) foundAction = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            else if (pkgName.equals("Calculator")) foundAction=getAppCategoryIntent(Intent.CATEGORY_APP_CALCULATOR);
            else if (pkgName.equals("Contacts")) foundAction=getAppCategoryIntent(Intent.CATEGORY_APP_CONTACTS);
            else if (pkgName.equals("Email")) foundAction=getAppCategoryIntent(Intent.CATEGORY_APP_EMAIL);
            else if (pkgName.equals("MusicPlayer") || pkgName.equals("Music")) foundAction=getAppCategoryIntent(Intent.CATEGORY_APP_MUSIC);
            else if (pkgName.equals("Phone")) { Intent intent = new Intent(Intent.ACTION_VIEW); intent.setData(Uri.parse("tel:")); foundAction=intent; }
            else if (pkgName.equals("Sms")) { Intent intent = new Intent(Intent.ACTION_VIEW); intent.setData(Uri.parse("sms:")); foundAction=intent; }
            else if (pkgName.equals("Navigation")) foundAction=getAppCategoryIntent(Intent.CATEGORY_APP_MAPS);


            // nie ma else!
        }
        else if (nlRecognizedIntent.equals("OpenWebAddress")) {
            String webAddress = slots.get("WebAddressPart");
            webAddress = webAddress.replaceAll(" ", ""); // detokenize
            foundAction = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+webAddress));
        }
        else if (nlRecognizedIntent.equals("SetAlarmForTime")) {
            boolean isElicitation = slots.get("ELICITATION_ACTION") == null ? false : true;
            if (isElicitation) throw new ElicitationException(slots.get("ELICITATION_ACTION"));
            else {
                int hour = Integer.parseInt(slots.get("Hour"));
                int minute = Integer.parseInt(slots.get("Minute"));
                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                i.putExtra(AlarmClock.EXTRA_HOUR, hour);
                i.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                i.putExtra(AlarmClock.EXTRA_VIBRATE, false);
                i.putExtra(AlarmClock.EXTRA_MESSAGE, "Wachet auf, ruft uns die Stimme!");
                return i;
            }
        }
        else if (nlRecognizedIntent.equals("NoOp")) {
            Log.d("NLUMODULE", "Client operation not supported");
        }

        return foundAction;
    }

}
