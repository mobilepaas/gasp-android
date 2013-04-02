package com.cloudbees.gasp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {
	private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        
		GoogleMap map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
 
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

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

            if (!Geocoder.isPresent())
              Log.i(TAG, "No geocoder available");
            else {
              try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
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
              } catch (IOException e) {
                Log.d(TAG, "IOException getting address from geocoder", e);
              }
            }
          }
        
        map.setMyLocationEnabled(true); 
        //map.addMarker(new MarkerOptions().position(
        //		new LatLng(location.getLatitude(), location.getLongitude())).title("My Location"));
        
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //map.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        
        CameraPosition cameraPosition = new CameraPosition.Builder()
        									.target(myLocation)
        									.zoom(16)
        									.bearing(0)
        									.tilt(0)
        									.build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
            		Log.i(TAG, restaurantName + ": " + addresses.get(0).toString());
            		
            		// Add marker to map
            		LatLng pos = new LatLng( addresses.get(0).getLatitude(),
            									addresses.get(0).getLongitude());
            		map.addMarker(new MarkerOptions().position(pos).title(restaurantName));    		
            	}
            }
        } catch (Exception e){
        	Log.d(TAG, "Error from Geocoder: Exception = " + e.getMessage());
        }
        //locationManager.requestLocationUpdates(provider, 2000, 10,
        //                                       locationListener);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
