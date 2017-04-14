package srtpl.com.gpslocationdisplaymap;

import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GPSTracker gps;
    TextView mTextView;
    String cityName = "";
    String stateName = "";
    String countryName = "";
    String address = "";
    double latitude;
    double longitude;
    // Google Map
    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);

        gps = new GPSTracker(MainActivity.this);
        // Check if GPS enabled
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            setLocation(latitude,longitude);
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
        initilizeMap();
    }

    Handler h = new Handler();
    int delay = 15000; //15 seconds
    Runnable runnable;
    @Override
    protected void onStart() {
//start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                gps.getLocation();
                if(gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                   setLocation(latitude,longitude);
                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }

                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onStart();
    }

    public void setLocation(double latitude,double longitude){
        Geocoder geocoder = new Geocoder(
                MainActivity.this, Locale
                .getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            address = addresses.get(0).getAddressLine(0)  +  " , " + addresses.get(0).getAddressLine(1);
            cityName = addresses.get(0).getAddressLine(2);
            countryName = addresses.get(0).getAddressLine(3);

            mTextView.setText("Your Location is - " +
                    "\n Address :- " +address +
                    "\n cityName :- " +cityName +
                    "\n countryName :- " +countryName +
                    "\nLat: " + latitude + "\nLong: " + longitude + "\n after 15 second your updated location display");
            Toast.makeText(getApplicationContext(), "Your Location is - " +
                    "\n Address :- " +address +
                    "\n cityName :- " +cityName +
                    "\n countryName :- " +countryName +
                    "\nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            // \n is for new line
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            MapFragment mMapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map));
            mMapFragment.getMapAsync(this);


            // check if map is created successfully or not
            if (mMapFragment == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng sydney = new LatLng(latitude,longitude);

        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setBuildingsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.addMarker(new MarkerOptions()
                .title(cityName)
                .snippet("The most populous city in " + countryName)
                .position(sydney));
    }


    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
