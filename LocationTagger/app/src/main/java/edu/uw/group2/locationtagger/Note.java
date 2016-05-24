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
    public double lat;
    public double lng;
    public int x;
    public int y;
    public boolean draw;
    public double distance;
    public double angle;

    public Note() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Note(String title, String description, long dateTime, LatLng location) {
        this("0", "anonymous", title, description, dateTime, location);
    }

    public Note(double lat, double lng) {
        this("0", "anonymous", "", "", 0, new LatLng(lat, lng));
    }

    public Note(String uid, String author, String title, String description, long dateTime, LatLng location) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.x = 0;
        this.y = 0;
        this.draw = false;
        this.distance = 0;
        this.angle = 0;
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

    public String getTitle(){
        return title;
    }

}
