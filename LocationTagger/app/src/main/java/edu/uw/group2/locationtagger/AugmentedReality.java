package edu.uw.group2.locationtagger;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.phenotype.Configuration;
import com.google.android.gms.vision.text.Text;

import java.io.IOException;
import java.util.ArrayList;

public class AugmentedReality extends AppCompatActivity implements SensorEventListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ValueEventListener{

    private static final String TAG = "AR";

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] mGravity;
    private float[] mGeomagnetic;

    private Camera camera;
    private CameraSurfaceView camPreview;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApi;
    private CameraOverlaySurfaceView overlay;

    private Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented_reality);

        //firebase setup
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase("https://location-tagger.firebaseio.com/notes/posts");
        firebaseRef.addValueEventListener(this);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (accelerometer == null || magnetometer == null) {
            Log.v(TAG, "Missing accelerometer and/or magnetometer");
            finish();
        }

//        camera = getCameraInstance();
//        camPreview = new CameraSurfaceView(this, camera);
//
//        // Create our Preview view and set it as the content of our activity.
//        FrameLayout framePreview = (FrameLayout) findViewById(R.id.framCamPreview);
//        framePreview.addView(camPreview);

        overlay = (CameraOverlaySurfaceView) findViewById(R.id.svOverlay);
        overlay.setZOrderOnTop(true);
        SurfaceHolder overlayHolder = overlay.getHolder();
        overlayHolder.setFormat(PixelFormat.TRANSLUCENT);

        if (googleApi == null) {
            googleApi = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApi.connect();
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        camera = getCameraInstance();
        camPreview = new CameraSurfaceView(this, camera);

        // Create our Preview view and set it as the content of our activity.
        FrameLayout framePreview = (FrameLayout) findViewById(R.id.framCamPreview);
        framePreview.addView(camPreview);

        camPreview.setVisibility(View.VISIBLE);


        super.onResume();
    }

    protected void onPause() {
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);

        camPreview.setVisibility((View.GONE));
        if (camera != null) {
            camera.release();
            camera = null;
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        googleApi.disconnect();
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Most of this code was taken from: http://www.codingforandroid.com/2011/01/using-orientation-sensors-simple.html
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float Rot[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(Rot, I, mGravity, mGeomagnetic);
            if (success) {
                //
                float orientation[] = new float[3];
                SensorManager.getOrientation(Rot, orientation); // Azimut, Pitch, Roll
//                TextView azimut = (TextView) findViewById(R.id.txtAzmiut);
//                azimut.setText(orientation[0] + "");
//
//                TextView pitch = (TextView) findViewById(R.id.txtPitch);
//                pitch.setText(orientation[1] + "");

                int cameraShift = 0;
                if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
                    if (orientation[2] < 0) {
                        cameraShift = -90;
                    } else if (orientation[2] > 0){
                        cameraShift = 90;
                    }
                }

                float rotationFloat = (-orientation[0] * 360 / (2 * 3.14159f)) + cameraShift;
                if (rotationFloat < 0 && rotationFloat < -180) {
                    rotationFloat = 180 - (-rotationFloat - 180);
                } else if (rotationFloat > 0 && rotationFloat > 180){
                    rotationFloat = -180 + (rotationFloat - 180);
                }

                overlay.roll = orientation[2];
                overlay.pitch = orientation[1];
                overlay.rotation = rotationFloat;

//                TextView roll = (TextView) findViewById(R.id.txtRoll);
//                roll.setText(orientation[2] + "");
//
//                TextView rotation = (TextView) findViewById(R.id.txtRotation);
//                rotation.setText("Rotation: " + rotationFloat);
//
//                TextView compass = (TextView) findViewById(R.id.txtCompass);
//                if (rotationFloat > -45 && rotationFloat< 45) {
//                    compass.setText("North");
//                } else if(rotationFloat > 45 && rotationFloat < 135) {
//                    compass.setText("West");
//                } else if (rotationFloat < -45 && rotationFloat > -135) {
//                    compass.setText("East");
//                } else {
//                    compass.setText("South");
//                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location changed");
        overlay.changeCurrentLocation(new Note(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApi, locationRequest, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<Note> notes = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            Log.v(TAG, child.toString());
            Note note = child.getValue(Note.class);
            notes.add(note);
        }
        overlay.notes = notes;
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}