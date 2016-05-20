package edu.uw.group2.locationtagger;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Note {

    public String uid;
    public String author;
    public String title;
    public String description;
    public long dateTime;
    public LatLng location;

    public Note() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Note(String uid, String author, String title, String description, long dateTime, LatLng locaton) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = locaton;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("description", description);
        result.put("dateTime", dateTime);
        result.put("lat", location.latitude);
        result.put("lng", location.longitude);

        return result;
    }

}
