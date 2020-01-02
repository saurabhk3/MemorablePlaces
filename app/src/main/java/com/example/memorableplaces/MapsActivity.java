package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    Geocoder gcoder;
    LocationManager lmanager;
    LocationListener listener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
                Location lastKnownLoc = lmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerOnMap(lastKnownLoc,"Your Last Location");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public void centerOnMap(Location location,String title) {  // draws a location on the map
        if (location != null) {

            LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLoc).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,13));
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        String address = "";
        gcoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = gcoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addressList!=null && addressList.size()>0){
                if(addressList.get(0).getLocality()!= null){
                    address += addressList.get(0).getLocality();
                }
            }
            if(address.equals("")){//add time
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
                address += sdf.format(new Date());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        MainActivity.places.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        lmanager = (LocationManager)getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                centerOnMap(location,"CurrentLoc");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(Build.VERSION.SDK_INT < 23){
            lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
            Location lastKnownLoc = lmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            centerOnMap(lastKnownLoc,"Your Last Location");
        }

        Intent intent = getIntent();
        Toast.makeText(this,Integer.toString(intent.getIntExtra("placeIndex",0)),Toast.LENGTH_SHORT).show();
    }
}
