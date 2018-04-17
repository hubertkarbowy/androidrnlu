package pl.hubertkarbowy.androidrnlu.config;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.hubert.myapplication.R;

/**
 * Created by hubert on 17.04.18.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
