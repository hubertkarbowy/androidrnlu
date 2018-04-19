package pl.hubertkarbowy.androidrnlu.util;

import android.content.Context;

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

    public static String getEffectiveSpokenText (String spokenText, String nlRecognizedIntentFromServer, Map<String, String> nlParams, boolean isIntentExists) {
        if (!spokenText.equals("*DELEGATED*")) return spokenText;
        else {
            if (nlRecognizedIntentFromServer.equals("AppLaunch")) {
                if (!isIntentExists) return "Nie mogę uruchomić tego polecenia na tej wersji telefonu"; // TODO KONIECZNIE: Externalize strings!
                else return "OK, uruchamiam " + nlParams.get("AppName"); // TODO KONIECZNIE: Externalize strings!
            }
            else return "Nie mogę delegować odpowiedzi do tej komendy"; // TODO KONIECZNIE: Externalize strings!
        }
    }
}
