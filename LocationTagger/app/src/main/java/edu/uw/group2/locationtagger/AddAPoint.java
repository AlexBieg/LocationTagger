package edu.uw.group2.locationtagger;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAPoint extends AppCompatActivity {

    private static final String TAG = "AddNewNote";

    private long tempTime;
    private Firebase mFirebaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private FirebaseUser user;

    private Location noteLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_apoint);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
//        mFirebaseRef = new Firebase(ProjectConstants.FIREBASE);
//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };

        TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText newTitle = ((EditText) findViewById(R.id.newPointTitle));
                EditText newDesc = ((EditText) findViewById(R.id.newDescription));
                Button newDateTime = ((Button) findViewById(R.id.newDateText));
                Button newSubmit = ((Button) findViewById(R.id.newSubmit));
                if (newTitle != null && newDesc != null && newDateTime != null && newSubmit != null) {
                    boolean isReady = (newTitle.getText().toString().trim().length() > 0)
                            && (newDesc.getText().toString().trim().length() > 0)
                            && !(newDateTime.getText().toString().equals(getString(R.string.default_datepicker_text)));
                    newSubmit.setEnabled(isReady);
                }
            }
        };

        EditText newTitle = ((EditText) findViewById(R.id.newPointTitle));
        EditText newDesc = ((EditText) findViewById(R.id.newDescription));
        Button newDateTime = ((Button) findViewById(R.id.newDateText));

        newTitle.addTextChangedListener(watcher);
        newDesc.addTextChangedListener(watcher);
        newDateTime.addTextChangedListener(watcher);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            Location loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(extras.getDouble("Lat"));
            loc.setLongitude(extras.getDouble("Lng"));
            noteLocation = loc;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.newMap);
        final LatLng currentPosition = new LatLng(noteLocation.getLatitude(), noteLocation.getLongitude());
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentPosition)
                        .zoom(17)
                        .build();
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setScrollGesturesEnabled(false);
                uiSettings.setZoomGesturesEnabled(false);
                googleMap.addMarker(new MarkerOptions()
                        .position(currentPosition)
                        .title("New Tag Will Go Here"));
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
        }
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog picker = new DatePickerDialog(getActivity(), this, year, month, day);
            return picker;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.clear();
            c.set(year, month, day);
            ((AddAPoint) getActivity()).tempTime = c.getTimeInMillis();
            TimePickerFragment picker = new TimePickerFragment();
            picker.show(getActivity().getSupportFragmentManager(), "timePicker");
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
            TimePickerDialog picker = new TimePickerDialog(getActivity(), this, hour, minute, false);
            return picker;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            long datePicked = ((AddAPoint) getActivity()).tempTime;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(datePicked);
            c.set(Calendar.HOUR, hour);
            c.set(Calendar.MINUTE, minute);
            ((AddAPoint) getActivity()).tempTime = c.getTimeInMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm aa", Locale.US);
            String dateString = dateFormat.format(c.getTimeInMillis());
            ((TextView) getActivity().findViewById(R.id.newDateText)).setText(dateString);
        }
    }

    public void openDatePicker(View v) {
        DatePickerFragment picker = new DatePickerFragment();
        picker.show(getSupportFragmentManager(), "datePicker");
    }

    public void submitNew(View v) {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference("notes");
        String key = ref.child("posts").push().getKey();
        String title = ((EditText) findViewById(R.id.newPointTitle)).getText().toString();
        String description = ((EditText) findViewById(R.id.newDescription)).getText().toString();
        long dateTime = tempTime;
        String userId = user.getUid();
        LatLng location = new LatLng(noteLocation.getLatitude(), noteLocation.getLongitude());
        String author = user.getEmail();
        Note note = new Note(userId, author, title, description, dateTime, location);
        Map<String, Object> postValues = note.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        ref.updateChildren(childUpdates);

        finish();

    }

}
