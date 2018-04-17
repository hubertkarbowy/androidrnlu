package pl.hubertkarbowy.androidrnlu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hubert.myapplication.R;

import java.util.ArrayList;
import java.util.Locale;

import pl.hubertkarbowy.androidrnlu.config.SettingsHelperMethods;
import pl.hubertkarbowy.androidrnlu.util.TCPClient;
import pl.hubertkarbowy.androidrnlu.util.TTSUtils;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "pl.hubertkarbowy.SimpleRnluClient.NLQUERY";
    public static TextView nlEditField;
    public static TextView servResponse;
    public static TTSUtils ttsInstance;
    TextToSpeech ttsobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        ttsInstance.closeTTS();
        super.onPause();
    }

    public void onDestroy() {
        ttsInstance.closeTTS();
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
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale(
                SettingsHelperMethods.readPrefs(getApplicationContext(), "culture", "pl_PL")));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.asr_prompt));
        try {
            startActivityForResult(intent, 100); // TODO: Remove hardcoding
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.asr_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void clearNlIntent(View view) {
        EditText nlInput = findViewById(R.id.nlInput);
        nlInput.setText("");
    }

    public void sendNlIntent(View view) {
        Handler handler = new Handler();

        String nlQuery = nlEditField.getText().toString();
        TCPClient query = new TCPClient(nlQuery, handler, this);
        query.sendQuery();
    }

    public void ttsTest(View view) {
        ttsobj.speak("Próba asystenta.", TextToSpeech.QUEUE_FLUSH, null);
    }

    public void setAndReadResponse(String displayText, String spokenText) {
        servResponse.setText(displayText);
        ttsobj.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);

        Log.d("DISPLAYTEXT", displayText);
        Log.d("SPOKENTEXT", spokenText);
    }
}
