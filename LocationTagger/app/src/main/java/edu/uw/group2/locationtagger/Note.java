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
    public int x;
    public int y;
    public boolean draw;
    public double distance;
    public double angle;
    public float fontSize;
    public double lat;
    public double lng;

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
        this.lat = location.latitude;
        this.lng = location.longitude;
        this.fontSize = 80;
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

    public static Note toNote(Map<String, Object> map) {
        Note note = new Note(
                (String) map.get("uid"),
                (String) map.get("author"),
                (String) map.get("title"),
                (String) map.get("description"),
                (long) map.get("dateTime"),
                new LatLng((double) map.get("lat"), (double) map.get("lng")));

        return note;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Note))
            return false;
        if (obj == this)
            return true;
        if (obj == null)
            return false;

        Note other = (Note) obj;

        boolean uids = this.uid.equals(other.uid);
        boolean authors = this.author.equals(other.author);
        boolean titles = this.title.equals(other.title);
        boolean descriptions = this.description.equals(other.description);
        boolean dates = this.dateTime == other.dateTime;
        boolean locations = this.location.equals(other.location);
        return uids && authors && titles && descriptions && dates && locations;
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
