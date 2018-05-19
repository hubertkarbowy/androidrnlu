package pl.hubertkarbowy.androidrnlu.util;

import java.util.Map;

/**
 * Created by hubert on 13.05.18.
 */

public class FulfilledIntent {
    public String displayText;
    public String spokenText;
    public String nlIntent;
    public Map<String, String> decodedNlIntentAndSlots;
    boolean isElicitation = false;

    public FulfilledIntent(String displayText, String spokenText, String nlIntent, Map<String, String> decodedNlIntentAndSlots, boolean isElicitation) {
        this.displayText = displayText;
        this.spokenText = spokenText;
        this.nlIntent = nlIntent;
        this.decodedNlIntentAndSlots = decodedNlIntentAndSlots;
        this.isElicitation = isElicitation;
    }
}
