package com.cloudbees.gasp;

import com.cloudbees.gasp.loader.RESTLoader;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * A simple ListActivity that displays Gasp! reviews
 * Closely modeled on Neil Goodman's REST loader tutorial
 * https://github.com/posco2k8/rest_loader_tutorial.git
 * Modified to remove Android support library
 * 
 * @author Mark Prichard
 *
 */
public class GaspReviewsActivity extends Activity 
		implements LoaderCallbacks<RESTLoader.RESTResponse> {
    private static final String TAG = GaspReviewsActivity.class.getName();
    
    private static final int LOADER_GASP_REVIEWS = 0x1;
    
    private static final String ARGS_URI    = "com.cloudbees.gasp.ARGS_URI";
    private static final String ARGS_PARAMS = "com.cloudbees.gasp.ARGS_PARAMS";
    
    private ArrayAdapter<String> mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_rest_loader);
        
        FragmentManager fm = getFragmentManager();
        ListFragment list =(ListFragment) fm.findFragmentById(R.id.fragment_content); 
        if (list == null){
        	list = new ListFragment();
        	FragmentTransaction ft = fm.beginTransaction();
        	ft.add(R.id.fragment_content, list);
        	ft.commit();
        }
        
        mAdapter = new ArrayAdapter<String>(this, R.layout.item_label_list);        
        list.setListAdapter(mAdapter);
        
        // TODO: Add Preference Fragment and Activity to allow user settings
        // For now, save REST server URI (@string/gasp_reviews_uri) in Shared Preferences
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor gaspSharedPreferencesEditor = gaspSharedPreferences.edit();
        gaspSharedPreferencesEditor.putString("gasp_reviews_uri", 
        		getResources().getString(R.string.gasp_reviews_uri)).commit();
      
        Log.i(TAG, "Using Gasp Server URI: " + gaspSharedPreferences.getString( "gasp_reviews_uri", "" ));
        
        Uri gaspReviewsUri = Uri.parse(gaspSharedPreferences.getString( "gasp_reviews_uri", "" ));
    
        // Loader arguments: LoaderManager will maintain the state of our Loaders
        // and reload if necessary. 
        Bundle args = new Bundle();
        Bundle params = new Bundle();
        args.putParcelable(ARGS_URI, gaspReviewsUri);
        args.putParcelable(ARGS_PARAMS, params);
        
        // Initialize the Loader.
        getLoaderManager().initLoader(LOADER_GASP_REVIEWS, args, this);
    }

    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
        if (args != null && args.containsKey(ARGS_URI) && args.containsKey(ARGS_PARAMS)) {
            Uri    action = args.getParcelable(ARGS_URI);
            Bundle params = args.getParcelable(ARGS_PARAMS);
            
            return new RESTLoader(this, RESTLoader.HTTPVerb.GET, action, params);
        }
        
        return null;
    }

    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {
        int    code = data.getCode();
        String json = data.getData();
        
        // Check to see if we got an HTTP 200 code and have some data.
        if (code == 200 && !json.equals("")) {
        	
        	Log.i( TAG, "gasp server returns:" + json );
           
        	JsonParser parser = new JsonParser();
        	JsonArray array = parser.parse(json).getAsJsonArray();
        	Iterator<JsonElement> reviews = array.iterator();

        	mAdapter.clear();
 
        	while (reviews.hasNext()) {
        		mAdapter.add(reviews.next().toString());
        	}
            
        }
        else {
        	Toast.makeText(this, "Failed to load reviews. Check your internet settings.", Toast.LENGTH_SHORT).show(); 
        }
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
    }
}