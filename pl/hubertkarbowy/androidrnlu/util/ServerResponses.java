package pl.hubertkarbowy.androidrnlu.util;

import android.content.Context;

import com.example.hubert.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hubert on 16.04.18.
 */

public class ServerResponses {

    public static int NO_ANDROID_INTENT = 0;
    public static int ANDROID_INTENT_FULFILLED = 1;
    public static int ANDROID_INTENT_ELICITATION = 2;

    public static String extractDisplayText(String serverResponse) {
        Pattern p = Pattern.compile("\\{DISPLAYTEXT:(.*?)\\}");
        Matcher m = p.matcher(serverResponse);
        if (m.find()) {
            return m.group(1);
        }
        else return null;
    }

    public static String extractSpokenText(String serverResponse) {
            Pattern p = Pattern.compile("\\{SPOKENTEXT:(.*?)\\}");
        Matcher m = p.matcher(serverResponse);
        if (m.find()) {
            return m.group(1);
        }
        else return null;
    }

    public static String extractNlIntent(String serverResponse) {
        Pattern p = Pattern.compile("\\{INTENT:(.*?)\\}");
        Matcher m = p.matcher(serverResponse);
        if (m.find()) {
            return m.group(1);
        }
        else return null;
    }

    public static Map<String, String> extractNlSlots(String serverResponse) {
        Map<String, String> slots = new HashMap<>();
        Pattern p = Pattern.compile("<(.*?):(.*?)>");
        Matcher m = p.matcher(serverResponse);
        while (m.find()) {
            slots.put(m.group(1), m.group(2));
        }
        return slots;
    }

    public static String getEffectiveSpokenText (String spokenText, String nlRecognizedIntentFromServer, Map<String, String> nlParams, int isIntentExists, Context ctxt) {
        if (!spokenText.equals("*DELEGATED*")) return spokenText;
        else {
            if (nlRecognizedIntentFromServer.equals("AppLaunch")) {
                if (isIntentExists==NO_ANDROID_INTENT) return ctxt.getString(R.string.noSuchAndroidIntent_S);
                else return ctxt.getString(R.string.nowRunning);
            }
            else return ctxt.getString(R.string.responseDelegationError);
        }
    }

    public static String getEffectiveDisplayText (String displayText, String nlRecognizedIntentFromServer, Map<String, String> nlParams, int isIntentExists, Context ctxt) {
        if (!displayText.equals("*DELEGATED*")) return displayText;
        else {
            if (nlRecognizedIntentFromServer.equals("AppLaunch")) {
                if (isIntentExists==NO_ANDROID_INTENT) return ctxt.getString(R.string.noSuchAndroidIntent_D);
                else return ctxt.getString(R.string.nowRunning) + " " + nlParams.get("AppName");
            }
            else return ctxt.getString(R.string.responseDelegationError);
        }
    }
}
