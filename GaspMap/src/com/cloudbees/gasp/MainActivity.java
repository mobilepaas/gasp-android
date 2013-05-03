package com.cloudbees.gasp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.cloudbees.gasp.model.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getName();

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
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
                    Log.i(TAG, geoLocation.getName());
                    Log.i(TAG, " " + geoLocation.getFormattedAddress());
                    Log.i(TAG, " " + String.valueOf(geoLocation.getLocation().getLng()));
                    Log.i(TAG, " " + String.valueOf(geoLocation.getLocation().getLat()));
                }
            }
            catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext());

        GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);
        Log.i(TAG, "Latitude = " + location.getLatitude());
        Log.i(TAG, "Longitude = " + location.getLongitude());

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());

            if (!Geocoder.isPresent()) Log.i(TAG, "No geocoder available");
            else {
                try {
                    List<Address> addresses = gc.getFromLocation(latitude,
                            longitude, 1);
                    StringBuilder sb = new StringBuilder();
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);

                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                            sb.append(address.getAddressLine(i)).append("\n");

                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                    }
                    Log.i(TAG, "Address: " + sb.toString());
                }
                catch (IOException e) {
                    Log.d(TAG, "IOException getting address from geocoder", e);
                }
            }
        }

        map.setMyLocationEnabled(true);
        // map.addMarker(new MarkerOptions().position(
        // new LatLng(location.getLatitude(),
        // location.getLongitude())).title("My Location"));

        LatLng myLocation = new LatLng(location.getLatitude(),
                location.getLongitude());
        // map.addMarker(new
        // MarkerOptions().position(myLocation).title("My Location"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation).zoom(16).bearing(0).tilt(0).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Use gasp-mongo REST service
        new LongRunningGetIO().execute();

        List<String> restaurants = new ArrayList<String>();
        restaurants.add("367 State Street, Los Altos");
        restaurants.add("236 Central Plaza Los Altos, CA");
        restaurants.add("161 Main St  Los Altos, CA");

        List<String> names = new ArrayList<String>();
        names.add("Peet's Coffee");
        names.add("Sumika Grill");
        names.add("Mikado Restaurant");

        try {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;

            for (int i = 0; i < names.size(); i++) {
                String restaurantName = names.get(i);
                String streetAddress = restaurants.get(i);

                // Use Geocoder to find full address + lat/long details
                addresses = gc.getFromLocationName(streetAddress, 1);

                if (addresses.size() > 0) {
                    Log.i(TAG, restaurantName + ": "
                            + addresses.get(0).toString());

                    // Add marker to map
                    LatLng pos = new LatLng(addresses.get(0).getLatitude(),
                            addresses.get(0).getLongitude());
                    map.addMarker(new MarkerOptions().position(pos).title(
                            restaurantName));
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG, "Error from Geocoder: Exception = " + e.getMessage());
        }
        // locationManager.requestLocationUpdates(provider, 2000, 10,
        // locationListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
