package edu.uw.group2.locationtagger;


import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gets preferences from xml
        addPreferencesFromResource(R.xml.preferences);
    }
}
