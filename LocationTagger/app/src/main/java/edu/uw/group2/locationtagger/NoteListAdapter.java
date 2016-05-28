package edu.uw.group2.locationtagger;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by PIGGY on 16/5/23.
 */
public class NoteListAdapter extends FirebaseListAdapter<Note> {

    public NoteListAdapter(Query ref, Activity activity, int layout){
        super(ref, Note.class, layout, activity);
    }

    @Override
    protected void populateView(View v, Note model) {
        String title = model.getTitle();
        TextView titleText = (TextView)v.findViewById(R.id.txtEventTitle);
        titleText.setText(title);
       // titleText.setText(title);
        //titleText.setText("hello");
        //System.out.println(title);
       // String description = model.getDescription();
       // System.out.println(description);

        long dateTime = model.getDateTime();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm aa", Locale.US);
        String date = format.format(dateTime);

        ((TextView) v.findViewById(R.id.txtDate)).setText(date);

    }
}