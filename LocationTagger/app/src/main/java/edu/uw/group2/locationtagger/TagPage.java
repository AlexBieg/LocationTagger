package edu.uw.group2.locationtagger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TagPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = getIntent().getExtras();
        final String title = args.getString("title");
        String description = args.getString("description");
        double lat = Double.parseDouble(args.getString("lat"));
        double lng = Double.parseDouble(args.getString("lng"));
        long date = Long.valueOf(args.getString("date"));

        ((TextView)findViewById(R.id.title)).setText(title);
        ((TextView)findViewById(R.id.description)).setText(description);

        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm aa", Locale.US);
        String dateTime = format.format(date);

        ((TextView)findViewById(R.id.date)).setText(dateTime);

        final LatLng noteLocation = new LatLng(lat, lng);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapLocation);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(noteLocation)
                        .zoom(17)
                        .build();
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setScrollGesturesEnabled(false);
                uiSettings.setZoomGesturesEnabled(false);
                googleMap.addMarker(new MarkerOptions()
                        .position(noteLocation)
                        .title(title));
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareEvent(View v) {

        Bundle args = getIntent().getExtras();
        final String title = args.getString("title");
        long date = Long.valueOf(args.getString("date"));
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm aa", Locale.US);
        String dateTime = format.format(date);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Tag Title: " + title + " Date: " + dateTime);

        Intent chooser = Intent.createChooser(intent, "Share This Tag");
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(chooser);
        }
    }
}
