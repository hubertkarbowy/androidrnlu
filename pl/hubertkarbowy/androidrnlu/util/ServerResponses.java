package pl.hubertkarbowy.androidrnlu.util;

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
}
