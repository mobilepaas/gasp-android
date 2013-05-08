package com.cloudbees.gasp.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.activity.GaspRESTServiceActivity;
import com.cloudbees.gasp.service.RESTService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GaspReviewsResponderFragment extends RESTResponderFragment {
    private static String TAG = GaspReviewsResponderFragment.class.getName();
    
    private ArrayAdapter<String> mAdapter;
    private List<String> mList;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // This gets called each time our Activity has finished creating itself.
        setReviews();
    }

    private void setReviews() {
        GaspRESTServiceActivity activity = (GaspRESTServiceActivity) getActivity();
        
        if (mAdapter == null && activity != null) {
            // This is where we make our REST call to the service. We also pass in our ResultReceiver
            // defined in the RESTResponderFragment super class.

            mAdapter = new ArrayAdapter<String>(activity, 0);
            mList = new ArrayList<String>();

            // Load shared preferences from res/xml/preferences.xml (first time only)
            // Subsequent activations will use the saved shared preferences from the device
            PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);
            SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.i(TAG, "Using Gasp Server URI: " + gaspSharedPreferences.getString( "gasp_endpoint_uri", "" ));           
            Uri gaspReviewsUri = Uri.parse(gaspSharedPreferences.getString( "gasp_endpoint_uri", "" ));
            
            // We will explicitly call our Service since we probably want to keep it as a private
            // component in our app. You could do this with Intent actions as well, but you have
            // to make sure you define your intent filters correctly in your manifest.
            Intent intent = new Intent(activity, RESTService.class);
            intent.setData(gaspReviewsUri);
            
            // Here we are going to place our REST call parameters. Note that
            // we could have just used Uri.Builder and appendQueryParameter()
            // here, but I wanted to illustrate how to use the Bundle params.
            Bundle params = new Bundle();
            
            intent.putExtra(RESTService.EXTRA_PARAMS, params);
            intent.putExtra(RESTService.EXTRA_RESULT_RECEIVER, getResultReceiver());
            
            // Here we send our Intent to our RESTService.
            activity.startService(intent);
        }
        else if (activity != null) {
            // Here we check to see if our activity is null or not.
            // We only want to update our views if our activity exists.
            
            ArrayAdapter<String> adapter = activity.getArrayAdapter();           
            adapter.clear();
            for (String tweet : mList) {
                adapter.add(tweet);
            }
            
        }
        else {
            Toast.makeText(activity, 
                           getResources().getString(R.string.gasp_network_error), 
                           Toast.LENGTH_SHORT)
                           .show(); 
        }
    }
    
    @Override
    public void onRESTResult(int code, String result) {
        // Here is where we handle our REST response. This is similar to the 
        // LoaderCallbacks<D>.onLoadFinished() call from the previous tutorial.
        
        // Check to see if we got an HTTP 200 code and have some data.
        if (code == 200 && result != null) {
            
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(result).getAsJsonArray();
            Iterator<JsonElement> reviews = array.iterator();

            mList.clear();
 
            while (reviews.hasNext()) {
                String theReview = reviews.next().toString();
                mList.add(theReview);
                Log.d(TAG, "Review: " + theReview);
            }
            
            setReviews();
        }
        else {
            Activity activity = getActivity();
            if (activity != null) {
                Toast.makeText(activity, 
                               getResources().getString(R.string.gasp_network_error),
                               Toast.LENGTH_SHORT)
                               .show();
            }
        }
    }
}
