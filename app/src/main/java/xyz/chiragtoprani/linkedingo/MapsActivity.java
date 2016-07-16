package xyz.chiragtoprani.linkedingo;

import android.location.Location;
import android.location.LocationListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng current;
    final float zoomLevel = 15.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }    // commybu


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng linkedIn = new LatLng(37.423917, -122.070828);
        mMap.addMarker(new MarkerOptions().position(linkedIn).title("Marker in LinkedIn"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(linkedIn));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(linkedIn, zoomLevel));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                current = point;
                mMap.addMarker(new MarkerOptions().position(point));
            }
        });
        LocationListener locationListener = new LocationListener() {
            // Called when a new location is found by the network location provider.
            public void onLocationChanged(Location location) {}

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider calling
            //      ActivityCombat#requestPermissions
            // here to request the missing permissions, and then overriding
            //      public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCombat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            Log.d("test", "ins");
            return;
        } else if(mMap != null){
            Log.d("test2", "outs");
            mMap.setMyLocationEnabled(true);
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

    /*
 * Private method to ensure that current location is properly enabled
 */
    private void setUpMap()
    {
        try {
            mMap.setMyLocationEnabled(true);
        } catch(SecurityException e)
        {
            e.printStackTrace();
        }
    }
}


