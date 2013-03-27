package com.cloudbees.gasp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferencesFragment extends PreferenceFragment {

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  // Load the preferences from res/xml/preferences.xml
	  addPreferencesFromResource(R.xml.preferences);
	 }
}
