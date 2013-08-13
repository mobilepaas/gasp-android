package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.fragment.GaspReviewsResponderFragment;

public class GaspRESTServiceActivity extends Activity {
    
    private ArrayAdapter<String> mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_service);
        
        mAdapter = new ArrayAdapter<String>(this, R.layout.item_label_list);
        
        FragmentManager     fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        ListFragment list = new ListFragment();
        ft.add(R.id.fragment_content, list);
        
        list.setListAdapter(mAdapter);
        
        // RESTResponderFragments call setRetainedInstance(true) in their onCreate() method. So that means
        // we need to check if our FragmentManager is already storing an instance of the responder.
        GaspReviewsResponderFragment responder = 
                (GaspReviewsResponderFragment) fm.findFragmentByTag("RESTResponder");
        if (responder == null) {
            responder = new GaspReviewsResponderFragment();
            
            // We add the fragment using a Tag since it has no views. It will make the Twitter REST call
            // for us each time this Activity is created.
            ft.add(responder, "RESTResponder");
        }

        ft.commit();
    }

    public ArrayAdapter<String> getArrayAdapter() {
        return mAdapter;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Single menu item only - need to handle multiple items if added
        Intent intent = new Intent();
        intent.setClass(GaspRESTServiceActivity.this, SetPreferencesActivity.class);
        startActivityForResult(intent, 0); 
        
        return true;
    }
}
