package edu.uw.group2.locationtagger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TagList extends AppCompatActivity {

    private final String TAG = "TAGLIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);

//        Firebase ref = new Firebase("https://location-tagger.firebaseio.com/notes/posts");
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.v(TAG, dataSnapshot.getChildrenCount() + " posts");
//                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Note post = postSnapshot.getValue(Note.class);
//                    Log.v(TAG, post.getTitle());
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        });


    }
}
