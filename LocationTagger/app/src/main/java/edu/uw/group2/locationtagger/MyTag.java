package edu.uw.group2.locationtagger;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MyTag extends AppCompatActivity {

    private static final String TAG = "MYTAG";

    private NoteListAdapter mNoteListAdapter;
    private String FIREBASE_URL =  ProjectConstants.FIREBASE + "notes/user-posts/";
    private Firebase mFirebaseRef;
    private FirebaseDatabase mDatabase;
    private ValueEventListener mConnectedListener;
    private FirebaseUser user;
    private Query queryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tag);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FIREBASE_URL += user.getUid();
        }else{
            FIREBASE_URL += "UsgczZKCzggcKFUxciiQUj560V72";
        }


        mFirebaseRef = new Firebase(FIREBASE_URL);
        queryRef = mFirebaseRef.orderByChild("dateTime");
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ListView listView = (ListView)findViewById(R.id.myTagListView);

        mNoteListAdapter = new NoteListAdapter(queryRef, this, R.layout.list_view);
        listView.setAdapter(mNoteListAdapter);
        mNoteListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mNoteListAdapter.getCount() - 1);
            }
        });


        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
//                    Toast.makeText(MyTag.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(MyTag.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
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
