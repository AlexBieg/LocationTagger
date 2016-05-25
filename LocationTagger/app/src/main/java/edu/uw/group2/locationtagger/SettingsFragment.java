package edu.uw.group2.locationtagger;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragment{

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gets preferences from xml
        addPreferencesFromResource(R.xml.preferences);

        mAuth = FirebaseAuth.getInstance();

        Preference button = (Preference)getPreferenceManager().findPreference("SignOut");
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    mAuth.signOut();
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("FirebaseUser", Context.MODE_PRIVATE).edit();
                    editor.remove("UserID");
                    editor.apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                    return true;
                }
            });
        }
    }
}
