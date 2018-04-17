package pl.hubertkarbowy.androidrnlu;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hubert.myapplication.R;

import pl.hubertkarbowy.androidrnlu.util.TCPClient;
import pl.hubertkarbowy.androidrnlu.util.UserInteraction;

import static pl.hubertkarbowy.androidrnlu.util.SimpleRnluClientSettings.locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "pl.hubertkarbowy.SimpleRnluClient.NLQUERY";
    public static TextView nlEditField;
    public static TextView servResponse;
    TextToSpeech ttsobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nlEditField = (TextView) findViewById(R.id.nlInput);
        servResponse = (TextView) findViewById(R.id.serverResponse);
        ttsobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttsobj.setLanguage(locale);
                }
                else {
                    UserInteraction.alertView(getApplicationContext(), "TTS error!");
                }
            }
        });

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, RnluSettingsScreen.class);
        EditText editText = (EditText) findViewById(R.id.nlInput);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void clearNlIntent(View view) {
        EditText nlInput = (EditText) findViewById(R.id.nlInput);
        nlInput.setText("");
    }

    public void sendNlIntent(View view) {
        Handler handler = new Handler();

        String nlQuery = nlEditField.getText().toString();
        TCPClient query = new TCPClient(nlQuery, handler, this);
        query.sendQuery();
    }

    public void ttsTest(View view) {
        ttsobj.speak("Dzień dobry. To jest głupi asystent głosowy.", TextToSpeech.QUEUE_FLUSH, null);
        UserInteraction.alertView(this, "WTF?");
    }

    public void setAndReadResponse(String displayText, String spokenText) {
        servResponse.setText(displayText);
        ttsobj.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);

        Log.d("DISPLAYTEXT", displayText);
        Log.d("SPOKENTEXT", spokenText);
    }

    public void onPause() {
        if (ttsobj != null) {
            ttsobj.stop();
            ttsobj.shutdown();
        }
        super.onPause();
    }
}
