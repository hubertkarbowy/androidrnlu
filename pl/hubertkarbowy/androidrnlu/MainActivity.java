package pl.hubertkarbowy.androidrnlu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.provider.AlarmClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hubert.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pl.hubertkarbowy.androidrnlu.config.SettingsHelperMethods;
import pl.hubertkarbowy.androidrnlu.nativeapps.NativeApps;
import pl.hubertkarbowy.androidrnlu.util.FulfilledIntent;
import pl.hubertkarbowy.androidrnlu.util.ServerResponses;
import pl.hubertkarbowy.androidrnlu.util.TCPClient;
import pl.hubertkarbowy.androidrnlu.util.TTSUtils;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "pl.hubertkarbowy.SimpleRnluClient.NLQUERY";
    public static TextView nlEditField;
    public static String continuationString;
    public static String elicitationTransducer;
    public static TextView servResponse;
    public static TTSUtils ttsInstance;
    TextToSpeech ttsobj;
    public int isElicitationExpected=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(SettingsHelperMethods.readPrefs(getApplicationContext(), "culture", "en_US"));  // for API versions < 17
        res.updateConfiguration(conf, dm);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        nlEditField = (TextView) findViewById(R.id.nlInput);
        servResponse = (TextView) findViewById(R.id.serverResponse);
    }

    public void onResume() {
        ttsInstance = new TTSUtils();
        ttsobj = ttsInstance.getTTSInstance(getApplicationContext());
        super.onResume();
    }
    public void onPause() {
       // ttsInstance.closeTTS();
        super.onPause();
    }

    public void onDestroy() {
       // ttsInstance.closeTTS();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) { // TODO: Remove hardcoding
            case 100: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    nlEditField.setText(result.get(0));
                    continuationString = null;
                }
                break;
            }
            case 101: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    continuationString = result.get(0);
                    StringBuilder continuationQuery = new StringBuilder();
                    continuationQuery.append("/ctxtcmd " + elicitationTransducer + " " + continuationString);
                    continuationString = continuationQuery.toString();
                    Log.d("CONTINUATION_QUERY", continuationString);
                    sendContinuationIntent(continuationString);
                    isElicitationExpected=0;
                    elicitationTransducer=null;
                    continuationString=null;
                }
                break;
            }
        }
    }

    /* ================================= CORE FUNCTIONS ================================ */

    public void openSettingsScreen(View view) {
//        Intent intent = new Intent(this, RnluSettingsScreen.class);
        Intent intent = new Intent(this, pl.hubertkarbowy.androidrnlu.config.SettingsActivity.class);
//        EditText editText = findViewById(R.id.nlInput);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void recordSound(View view) {
        speechToText(100);
    }

    public void clearNlIntent(View view) {
        EditText nlInput = findViewById(R.id.nlInput);
        nlInput.setText("");
    }

    public void sendNlIntent(View view) {
        Handler handler = new Handler();

        String nlQuery = nlEditField.getText().toString();
        TCPClient query = new TCPClient(nlQuery, handler, this);
        FulfilledIntent f = query.sendQuery();
        if (f != null) {
            setAndReadResponse(f.displayText, f.spokenText, f.nlIntent, f.decodedNlIntentAndSlots);
        }

        if (isElicitationExpected==2) {
            while (ttsobj.isSpeaking()) {}
            Log.d("CONTINUATION_TRANSDUCER", elicitationTransducer);
            speechToText(101);
        }
        else {
            Log.d("NIEMA", "Nie ma nic w kontekscie");
        }
        continuationString = null; // reset dialog
    }

    public void sendContinuationIntent(String contextCommand) {
        Handler handler = new Handler();

        TCPClient query = new TCPClient(contextCommand, handler, this, true);
        FulfilledIntent f = query.sendQuery();
        FulfilledIntent fExecutable = null;
        if (f != null) {
            fExecutable = setAndReadResponse(f.displayText, f.spokenText, f.nlIntent, f.decodedNlIntentAndSlots);
            while (ttsobj.isSpeaking()) {}
            if (fExecutable != null) {
                executeIntent(fExecutable.nlIntent, fExecutable.decodedNlIntentAndSlots);
            }
        }
        else {
            Log.d("CONTINUATION_RESULT", "Nie trafilo sie z kontynuacja...");
        }

        continuationString = null; // reset dialog
    }

    public void setAlarm(View view) {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, 10);
        i.putExtra(AlarmClock.EXTRA_MINUTES, 10);
        i.putExtra(AlarmClock.EXTRA_VIBRATE, false);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Wachet auf, ruft uns die Stimme!");
        startActivity(i);
    }

    public void ttsTest(View view) {
        ttsobj.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                // Toast.makeText(getApplicationContext(), "Poczatek!",Toast.LENGTH_SHORT).show();
                Log.d("TTSOUT", "Poczatek");
            }

            @Override
            public void onDone(String s) {

                               // Toast.makeText(getApplicationContext(), "Przeczytano!",Toast.LENGTH_SHORT).show();
                                Log.d("TTSOUT","Gotowe");

            }

            @Override
            public void onError(String s) {

            }
        });

        HashMap<String, String> dummyMap = new HashMap<>();
        dummyMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        ttsobj.speak("Próba asystenta.", TextToSpeech.QUEUE_FLUSH, dummyMap);
    }

    public FulfilledIntent setAndReadResponse(String displayText, String spokenText, String nlRecognizedIntentFromServer, final Map<String, String> nlParams) {
        Intent preTest = null;
        int preIsFulfilledIntentExists = 0;
        final int isFulfilledIntentExists;

        try {
            preTest = NativeApps.fetchIntent(nlRecognizedIntentFromServer, nlParams);
            if (preTest != null && preTest.resolveActivity(getPackageManager()) != null) preIsFulfilledIntentExists=ServerResponses.ANDROID_INTENT_FULFILLED;
            //  boolean isIntentExists = preTest.resolveActivity(getPackageManager()) == null ? false : true;
        }
        catch (ElicitationException e) {
            preIsFulfilledIntentExists=ServerResponses.ANDROID_INTENT_ELICITATION;
            elicitationTransducer = e.getMessage();
        }
        isFulfilledIntentExists = preIsFulfilledIntentExists;


        final String effectiveNlIntent = isFulfilledIntentExists == ServerResponses.ANDROID_INTENT_FULFILLED ? nlRecognizedIntentFromServer : "NoOp";
        String effectiveDisplayText = ServerResponses.getEffectiveDisplayText(displayText, nlRecognizedIntentFromServer, nlParams, isFulfilledIntentExists, getApplicationContext());
        String effectiveSpokenText = ServerResponses.getEffectiveSpokenText(spokenText, nlRecognizedIntentFromServer, nlParams, isFulfilledIntentExists, getApplicationContext());


        servResponse.setText(effectiveDisplayText);
        HashMap<String, String> dummyMap = new HashMap<>();
        dummyMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "GoGetIt");
        Log.d("IS_ELICITATION_EXPECTED", ""+isFulfilledIntentExists);
        Log.d("EFFECTIVE_NL_INTENT", effectiveNlIntent);
        Log.d("EFFECTIVE_NL_PARAMS", nlParams.toString());
        if (isFulfilledIntentExists==ServerResponses.ANDROID_INTENT_ELICITATION) {
            isElicitationExpected = 2;
        }

        ttsobj.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
            }

            @Override
            public void onDone(String s) {
                if (isFulfilledIntentExists==ServerResponses.ANDROID_INTENT_ELICITATION) {
                    isElicitationExpected=2;
                }
                else {
                    executeIntent(effectiveNlIntent, nlParams);
                    isElicitationExpected=1;
                }
                Log.d("TTS_ON_DONE", "TTS finished");
            }

            @Override
            public void onError(String s) {

            }

        });
        ttsobj.speak(effectiveSpokenText, TextToSpeech.QUEUE_FLUSH, dummyMap);

        Log.d("DISPLAYTEXT", effectiveDisplayText);
        Log.d("SPOKENTEXT", effectiveSpokenText);
        if (effectiveSpokenText.equals("Unknown command")) return null; // this is a hack...
        else return new FulfilledIntent(effectiveDisplayText,effectiveSpokenText,effectiveNlIntent,nlParams,false);
    }

    public void executeIntent(String nlRecognizedIntent, Map<String, String> nlParams) {
        Log.d("INTENT_FULFILLMENT", "Inside executeIntent");
        Intent foundAction = NativeApps.fetchIntent(nlRecognizedIntent, nlParams);
            if (foundAction.resolveActivity(getPackageManager()) != null) startActivity(foundAction);
            else Log.d("INTENT_FULFILLMENT", "Nie bangla");

    }

    /* ---------------------------------- STĄÐ ---------------------------------- */

    public String speechToText (int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale(
                SettingsHelperMethods.readPrefs(getApplicationContext(), "culture", "pl_PL")));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please start speaking");
        try {
            startActivityForResult(intent, code); // TODO: Remove hardcoding
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.asr_error), Toast.LENGTH_SHORT).show();
        }
        if (code==100) return nlEditField.getText().toString();
        else if (code==101) return continuationString;
        else return "WTF?";
    }
}
