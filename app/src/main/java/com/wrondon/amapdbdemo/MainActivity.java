package com.wrondon.amapdbdemo;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String LOG_TAG = "MainActivity";

    private final DatabaseReference ref =
            FirebaseDatabase.getInstance().getReference("marker");

    private GoogleMap mMap;

    private Double lat=27.76;           //default;
    private Double lng=-82.64;

    private Double lat_fixed=27.76;     // St Pete, FL
    private Double lng_fixed=-82.64;    // St Pete, FL

    private LatLng coordMarker1;
    private LatLng coordMarker2;

    private Marker marker1;
    private Marker marker2;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mMap.clear();
                    coordMarker1 = new LatLng(lat_fixed, lng_fixed);
                    marker1 = mMap.addMarker(new MarkerOptions().position(coordMarker1).title("Marker in St Pete - FL (Hardcoded)"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(coordMarker1));
                    return true;
                case R.id.navigation_dashboard:
                    coordMarker2 = new LatLng(lat, lng);
                    marker2 = mMap.addMarker(new MarkerOptions().position(coordMarker2).title("Marker from DB (Dynamic)"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(coordMarker2));
                    return true;
                case R.id.navigation_notifications:
                    mMap.clear();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MarkerViewModel viewModel = ViewModelProviders.of(this).get(MarkerViewModel.class);

        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();

        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    lat = dataSnapshot.child("lat").getValue(Double.class);
                    lng = dataSnapshot.child("lng").getValue(Double.class);
                    if (marker2!=null){
                        marker2.setPosition(new LatLng(lat, lng));
                    }

                }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(10.0f);

        LatLng marker = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(marker).title("Marker in St Pete - FL"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {

                Toast.makeText(getApplicationContext(), "( id / title ) : >> "+m.getId()+" / "+m.getTitle(), Toast.LENGTH_SHORT ).show();
                return true;
            }
        });
    }
}
