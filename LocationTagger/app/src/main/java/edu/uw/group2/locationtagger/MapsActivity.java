package edu.uw.group2.locationtagger;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ValueEventListener {

    private static final String TAG = "Map Activity";

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApi;

    private Firebase firebaseRef;

    private ArrayList<Note> notes;

    private Location currentLocation;

    private boolean firstLocationUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        firstLocationUpdate = true;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get location
        if (googleApi == null) {
            googleApi = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(ProjectConstants.FIREBASE + "notes/posts");
        firebaseRef.addValueEventListener(this);

        notes = new ArrayList<>();

        Button arButton = (Button) findViewById(R.id.btnAR);
        arButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AugmentedReality.class));
            }
        });

        Button mapButton = (Button) findViewById(R.id.btnMap);
        mapButton.setEnabled(false);

        Button listButton = (Button) findViewById(R.id.btnList);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, TagList.class));
            }
        });

        FloatingActionButton addTag = (FloatingActionButton) findViewById(R.id.fabAddTag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, AddAPoint.class);
                intent.putExtra("Lat", currentLocation.getLatitude());
                intent.putExtra("Lng", currentLocation.getLongitude());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        googleApi.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAR:
                startActivity(new Intent(this, AugmentedReality.class));
                return true;
            case R.id.menu_new_note:
                Intent newNote = new Intent(this, AddAPoint.class);
                Bundle extras = new Bundle();
                double lat = currentLocation.getLatitude();
                double lng = currentLocation.getLongitude();
                extras.putDouble("Lat", lat);
                extras.putDouble("Lng", lng);
                newNote.putExtras(extras);

                startActivity(newNote);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.tagList:
                startActivity(new Intent(this, TagList.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        googleApi.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApi, locationRequest, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        Location last = LocationServices.FusedLocationApi.getLastLocation(googleApi);
        if (last != null) {
            LatLng loc = new LatLng(last.getLatitude(), last.getLongitude());
            setMapView(loc);
        }
    }

    public void updateMap(){
        Log.v(TAG, "Updating Map");
        for (Note note : notes) {
            LatLng noteLocation = new LatLng(note.lat, note.lng);
            mMap.addMarker(new MarkerOptions().position(noteLocation).title(note.title));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (firstLocationUpdate) {
            LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
            setMapView(curr);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 15));
            firstLocationUpdate = false;
        }
        currentLocation = location;
    }

    public void setMapView(LatLng location) {
        if(location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(17)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Log.v(TAG, location.toString());
        } else {
            Log.v(TAG, "Received null location");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.v(TAG, "Firebased loaded up!!!!!!");
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            Log.v(TAG, child.toString());
            Note note = child.getValue(Note.class);
            notes.add(note);
        }
        updateMap();
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

}
