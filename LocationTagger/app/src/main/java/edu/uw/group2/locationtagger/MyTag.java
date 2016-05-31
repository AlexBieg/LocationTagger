package edu.uw.group2.locationtagger;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class MyTag extends AppCompatActivity {

    private static final String TAG = "MYTAG";

    private NoteListAdapter mNoteListAdapter;
    private String FIREBASE_URL =  ProjectConstants.FIREBASE + "notes/user-posts/";
    private Firebase ref;
    private Firebase mFirebaseRef;
    private FirebaseDatabase mDatabase;
    private ValueEventListener mConnectedListener;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tag);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FIREBASE_URL += user.getUid();
        }


        mFirebaseRef = new Firebase(FIREBASE_URL);
        mDatabase = FirebaseDatabase.getInstance();

        Button arButton = (Button) findViewById(R.id.btnAR);
        arButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyTag.this, AugmentedReality.class));
                finish();
            }
        });

        Button mapButton = (Button) findViewById(R.id.btnMap);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyTag.this,MapsActivity.class));
                finish();
            }
        });

        Button listButton = (Button) findViewById(R.id.btnList);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MyTag.this, TagList.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final ListView listView = (ListView)findViewById(R.id.myTagListView);

        mNoteListAdapter = new NoteListAdapter(mFirebaseRef, this, R.layout.list_view);
        listView.setAdapter(mNoteListAdapter);
        mNoteListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mNoteListAdapter.getCount() - 1);
            }
        });

        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Log.v("LIST", snapshot.getValue().toString());

                    HashMap<String, Object> note = ((HashMap<String, Object>) snapshot.getValue());
                    long dateTime = (long) note.get("dateTime");
                    Calendar current = Calendar.getInstance();
                    Calendar nextDay = Calendar.getInstance();
                    nextDay.setTimeInMillis(dateTime);
                    nextDay.add(Calendar.DATE, 1);
                    long nextMillis = nextDay.getTimeInMillis();

                    Log.v("LIST", (nextMillis - current.getTimeInMillis()) + "");
                    if (nextMillis < current.getTimeInMillis()) {
                        Firebase toRemove = snapshot.getRef();
                        toRemove.removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(MyTag.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyTag.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note current = (Note)parent.getItemAtPosition(position);
                Intent intent = new Intent(MyTag.this, TagPage.class);
                intent.putExtra("title", current.getTitle());
                intent.putExtra("description", current.getDescription());
                intent.putExtra("lat", current.getLat() + "");
                intent.putExtra("lng", current.getLng() + "");
                intent.putExtra("date", current.getDateTime() + "");

                startActivity(intent);

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

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mNoteListAdapter.cleanup();
    }
}
