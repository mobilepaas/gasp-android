package com.cloudbees.gasp.activity;

import com.cloudbees.gasp.fragment.PreferencesFragment;

import android.app.Activity;
import android.os.Bundle;

public class SetPreferencesActivity extends Activity {
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 getFragmentManager().beginTransaction().replace(android.R.id.content,
	                new PreferencesFragment()).commit();
	 }
}
