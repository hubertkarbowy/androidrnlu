package pl.hubertkarbowy.androidrnlu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by hubert on 17.04.18.
 */

public class TTSUtils {

    TextToSpeech ttsobj;

    public TextToSpeech getTTSInstance(final Context ctxt) {
        ttsobj = new TextToSpeech(ctxt, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
                    String storedLocale = prefs.getString("culture", "pl_PL");
                    Log.d("ALLPREFS", "" + prefs.getAll());
                    Log.d("NEWTTS", "instance of tts => " + storedLocale);
                    ttsobj.setLanguage(new Locale(storedLocale));
                }
                else {
                    UserInteraction.alertView(ctxt, "TTS error!");
                }
            }
        });
        return ttsobj;
    }

    public void closeTTS() {
        if (ttsobj != null) {
            ttsobj.stop();
            ttsobj.shutdown();
        }
    }
}
