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

    public Note() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Note(String title, String description, long dateTime, LatLng location) {
        this("0", "anonymous", title, description, dateTime, location);

    }

    public Note(String uid, String author, String title, String description, long dateTime, LatLng location) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.lat = location.latitude;
        this.lng = location.longitude;

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
    public String getUid() {
        return uid;
    }
    public String getAuthor() {
        return author;
    }
    public String getDescription(){
        return description;
    }
    public long getDateTime(){
        return dateTime;
    }
    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

}
