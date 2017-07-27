package com.example.android.nouvelles;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    // Fragment containing the preferences
    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // We use settings_main.xml as a preference resource file
            addPreferencesFromResource(R.xml.settings_main);
            /* Then we use the key of a preference inside of PreferenceFragment's
             *  findPreference() method to get the Preference object, and set up the preference
             *  using a helper method called bindPreferenceSummaryToValue() */
            Preference searchQuery = findPreference(getString(R.string.settings_search_query_key));
            bindPreferenceToValue(searchQuery);
            Preference orderBy = findPreference(getString(R.string.settings_order_by_list_key));
            bindPreferenceToValue(orderBy);
        }

        /**
         * Helper method to set the current {@link NewsPreferenceFragment} instance as the
         * listener on each preference. We also read the current value of the preference
         * stored in the SharedPreferences on the device, and display that in the preference
         * summary ( so that the user can see the current value of the preference).
         *
         * @param preference
         */
        public void bindPreferenceToValue(Preference preference) {
            // Set an preference change listener
            preference.setOnPreferenceChangeListener(this);
            // Get an instance of SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            // Retrieve default value
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            // And show that value
            onPreferenceChange(preference, preferenceString);
        }

        // Properly update the summary of a ListPreference (using the label, instead of the key).
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // Store the new value
            String stringValue = newValue.toString();
            // If this is a list preference, use the value instead of keys
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            // And update the summary
            preference.setSummary(stringValue);
            return true;
        }
    }
}
