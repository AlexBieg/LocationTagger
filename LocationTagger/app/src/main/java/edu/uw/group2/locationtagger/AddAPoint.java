package edu.uw.group2.locationtagger;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddAPoint extends AppCompatActivity {

    private static final String TAG = "AddNewNote";

    private long tempTime;
    private Firebase mFirebaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_apoint);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://fourth-splice-131619.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

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
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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

            // Create a new instance of DatePickerDialog and return it
            TimePickerDialog picker = new TimePickerDialog(getActivity(), this, 0, 0, false);
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm aa");
            String dateString = dateFormat.format(c.getTimeInMillis());
            ((TextView) getActivity().findViewById(R.id.newDateText)).setText(dateString);
        }
    }

    public void openDatePicker(View v) {
        DatePickerFragment picker = new DatePickerFragment();
        picker.show(getSupportFragmentManager(), "datePicker");
    }

    public void submitNew(View v) {
//        mAuth.createUserWithEmailAndPassword("test@test.com", "password")
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                        // ...
//                    }
//                });
//        mAuth.signInWithEmailAndPassword("test@test.com", "password")
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithEmail", task.getException());
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                        // ...
//                    }
//                });
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference("notes");
        String key = ref.child("posts").push().getKey();
        String title = ((EditText) findViewById(R.id.newPointTitle)).getText().toString();
        String description = ((EditText) findViewById(R.id.newDescription)).getText().toString();
        long dateTime = tempTime;
        String userId = "0";
        LatLng location = new LatLng(100.0, 100.0);
        Note note = new Note(title, description, dateTime, location);
        Map<String, Object> postValues = note.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        ref.updateChildren(childUpdates);

        finish();

    }

}
