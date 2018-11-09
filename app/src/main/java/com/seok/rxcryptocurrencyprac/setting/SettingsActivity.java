package com.seok.rxcryptocurrencyprac.setting;

import android.os.Bundle;

import com.seok.rxcryptocurrencyprac.R;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
