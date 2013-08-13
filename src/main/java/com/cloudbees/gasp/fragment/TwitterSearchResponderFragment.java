/*
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.gasp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.activity.TwitterRESTServiceActivity;
import com.cloudbees.gasp.service.RESTService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Closely modeled on Neil Goodman's Android REST tutorials
 * https://github.com/posco2k8/rest_service_tutorial
 * https://github.com/posco2k8/rest_loader_tutorial.git
 *
 * @author Mark Prichard
 */
public class TwitterSearchResponderFragment extends RESTResponderFragment {
    private static final String TAG = TwitterSearchResponderFragment.class.getName();
    
    // We cache our stored tweets here so that we can return right away
    // on multiple calls to setTweets() during the Activity lifecycle events (such
    // as when the user rotates their device). In a real application we would want
    // to cache this data in a more sophisticated way, probably using SQLite and
    // Content Providers, but for the demo and simple apps this will do.
    private List<String> mTweets;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // This gets called each time our Activity has finished creating itself.
        setTweets();
    }

    private void setTweets() {
        TwitterRESTServiceActivity activity = (TwitterRESTServiceActivity) getActivity();
        
        if (mTweets == null && activity != null) {
            // This is where we make our REST call to the service. We also pass in our ResultReceiver
            // defined in the RESTResponderFragment super class.
            
            // We will explicitly call our Service since we probably want to keep it as a private
            // component in our app. You could do this with Intent actions as well, but you have
            // to make sure you define your intent filters correctly in your manifest.
            Intent intent = new Intent(activity, RESTService.class);
            intent.setData(Uri.parse("http://search.twitter.com/search.json"));
            
            // Here we are going to place our REST call parameters. Note that
            // we could have just used Uri.Builder and appendQueryParameter()
            // here, but I wanted to illustrate how to use the Bundle params.
            Bundle params = new Bundle();
            params.putString("q", "cloudbees");
            
            intent.putExtra(RESTService.EXTRA_PARAMS, params);
            intent.putExtra(RESTService.EXTRA_RESULT_RECEIVER, getResultReceiver());
            
            // Here we send our Intent to our RESTService.
            activity.startService(intent);
        }
        else if (activity != null) {
            // Here we check to see if our activity is null or not.
            // We only want to update our views if our activity exists.
            
            ArrayAdapter<String> adapter = activity.getArrayAdapter();
            
            // Load our list adapter with our Tweets.
            adapter.clear();
            for (String tweet : mTweets) {
                adapter.add(tweet);
            }
        }
    }
    
    @Override
    public void onRESTResult(int code, String result) {
        // Here is where we handle our REST response. This is similar to the 
        // LoaderCallbacks<D>.onLoadFinished() call from the previous tutorial.
        
        // Check to see if we got an HTTP 200 code and have some data.
        if (code == 200 && result != null) {
            
            // For really complicated JSON decoding I usually do my heavy lifting
            // with Gson and proper model classes, but for now let's keep it simple
            // and use a utility method that relies on some of the built in
            // JSON utilities on Android.
            mTweets = getTweetsFromJson(result);
            setTweets();
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
    
    private static List<String> getTweetsFromJson(String json) {
        ArrayList<String> tweetList = new ArrayList<String>();
        
        try {
            JSONObject tweetsWrapper = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray  tweets        = tweetsWrapper.getJSONArray("results");
            
            for (int i = 0; i < tweets.length(); i++) {
                JSONObject tweet = tweets.getJSONObject(i);
                Log.d(TAG, "Tweet: " + tweet.getString("text"));
                tweetList.add(tweet.getString("text"));
            }
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON.", e);
        }
        
        return tweetList;
    }

}
