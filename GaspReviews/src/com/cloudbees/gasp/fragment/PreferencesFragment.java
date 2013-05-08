package com.cloudbees.gasp.fragment;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.R.xml;

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
