package com.cloudbees.gasp.activity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.loader.RESTLoader;
import com.cloudbees.gasp.model.GeoLocation;
import com.cloudbees.gasp.model.Location;
import com.cloudbees.gasp.model.SpatialQuery;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * A simple ListActivity that displays Gasp! reviews
 * Closely modeled on Neil Goodman's REST loader tutorial
 * https://github.com/posco2k8/rest_loader_tutorial.git
 * Modified to remove Android support library
 * 
 * @author Mark Prichard
 *
 */
public class GaspRESTLoaderActivity extends Activity 
		implements LoaderCallbacks<RESTLoader.RESTResponse> {
    private static final String TAG = GaspRESTLoaderActivity.class.getName();
    
    private static final int LOADER_GASP_REVIEWS = 0x1;
    
    private static final String ARGS_URI    = "com.cloudbees.gasp.ARGS_URI";
    private static final String ARGS_PARAMS = "com.cloudbees.gasp.ARGS_PARAMS";
    
    private ArrayAdapter<String> mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set main content view for Gasp! Reviews
        setContentView(R.layout.gasp_review_activity);
        
        // Use the Fragments API to display review data
        FragmentManager fm = getFragmentManager();
        ListFragment list =(ListFragment) fm.findFragmentById(R.id.gasp_review_content); 
        if (list == null){
        	list = new ListFragment();
        	FragmentTransaction ft = fm.beginTransaction();
        	ft.add(R.id.gasp_review_content, list);
        	ft.commit();
        }
        
        // Array adapter provides access to the review list data
        mAdapter = new ArrayAdapter<String>(this, R.layout.gasp_review_list);        
        list.setListAdapter(mAdapter);
        
        // Load shared preferences from res/xml/preferences.xml (first time only)
        // Subsequent activations will use the saved shared preferences from the device
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      
        Log.i(TAG, "Using Gasp Server URI: " + gaspSharedPreferences.getString( "gasp_endpoint_uri", "" ));
        Uri gaspReviewsUri = Uri.parse(gaspSharedPreferences.getString( "gasp_endpoint_uri", "" ));
    
        // Loader arguments: LoaderManager will maintain the state of our Loaders
        // and reload if necessary. 
        Bundle args = new Bundle();
        Bundle params = new Bundle();
        args.putParcelable(ARGS_URI, gaspReviewsUri);
        args.putParcelable(ARGS_PARAMS, params);
        
        // Initialize the Loader.
        getLoaderManager().initLoader(LOADER_GASP_REVIEWS, args, this);
        
        // Use gasp-mongo REST service
        new ReviewsRequest().execute();
        
        try{
            LocationsRequest locations = new LocationsRequest( 
                                            new SpatialQuery( 
                                               new Location( -122.1139858, 37.3774655), 
                                               0.005 ));

            List<GeoLocation> locationList = locations.execute().get();
            Iterator<GeoLocation> iterator = locationList.iterator();
            while(iterator.hasNext()){
                GeoLocation geoLocation = iterator.next();
                Log.i(TAG, geoLocation.getName());
                Log.i(TAG, " " + geoLocation.getFormattedAddress());
                Log.i(TAG, " " + String.valueOf(geoLocation.getLocation().getLng()));
                Log.i(TAG, " " + String.valueOf(geoLocation.getLocation().getLat()));
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
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
        	
        	Log.i( TAG, "RESTLoader returns:" + json );
           
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_settings, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Single menu item only - need to handle multiple items if added
		Intent intent = new Intent();
        intent.setClass(GaspRESTLoaderActivity.this, SetPreferencesActivity.class);
        startActivityForResult(intent, 0); 
		
        return true;
	}
	
    private class ReviewsRequest extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(
                    "http://gasp-mongo.mqprichard.cloudbees.net/locations/get");
            String text = null;
            try {
                HttpResponse response = httpClient.execute(httpGet,
                        localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
                Log.i(TAG, text);
                Gson gson = new Gson();
                Type type = new TypeToken<List<GeoLocation>>(){}.getType();
                List<GeoLocation> list = gson.fromJson(text, type);
                Iterator<GeoLocation> iterator = list.iterator();
                while(iterator.hasNext()){
                    GeoLocation geoLocation = iterator.next();
                    Log.d(TAG, geoLocation.getName());
                    Log.d(TAG, " " + geoLocation.getFormattedAddress());
                    Log.d(TAG, " " + String.valueOf(geoLocation.getLocation().getLng()));
                    Log.d(TAG, " " + String.valueOf(geoLocation.getLocation().getLat()));
                }
            }
            catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }
    }
    
    private class LocationsRequest extends AsyncTask<Void, Void, List<GeoLocation>> {
        
        private final String requestURI = "http://gasp-mongo.mqprichard.cloudbees.net/locations/geocenter";
        private String requestBody = null;
        private String responseBody = null;
        private List<GeoLocation> geoLocations = null;

        public LocationsRequest(SpatialQuery query) {
            super();      
            this.setRequestBody(new Gson().toJson(query).toString());
        }

        private String getRequestURI() {
            return requestURI;
        }

        private void setRequestBody(String requestBody) {
            this.requestBody = requestBody;
        }

        private String getResponseBody() {
            return responseBody;
        }

        private void setGeoLocations(List<GeoLocation> geoLocations) {
            this.geoLocations = geoLocations;
        }

        public List<GeoLocation> getGeoLocations() {
            return geoLocations;
        }
        
        private void parseJson() {
            Gson gson = new Gson();
            Type type = new TypeToken<List<GeoLocation>>(){}.getType();
            List<GeoLocation> locationList = gson.fromJson(getResponseBody(), type);
            this.setGeoLocations(locationList);    
        }

        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected List<GeoLocation> doInBackground(Void... params) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();

            try {
                HttpPost httpPost = new HttpPost(this.getRequestURI());
                httpPost.addHeader("Accept", "application/json");
 
                StringEntity input = new StringEntity(requestBody);
                input.setContentType("application/json");
                httpPost.setEntity(input);

                HttpResponse response = httpClient.execute(httpPost,localContext);
                HttpEntity entity = response.getEntity();
                responseBody = getASCIIContentFromEntity(entity);
                Log.i(TAG, responseBody);
                this.parseJson();
            }
            catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            return this.getGeoLocations();
        }
    }    
}