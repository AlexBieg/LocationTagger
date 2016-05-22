package edu.uw.group2.locationtagger;

/**
 * Created by alexb on 5/20/2016.
 */
public class Tag {
    public double lat;
    public double lng;
    public String text;
    public double angle;
    public double distance;
    public int x;
    public int y;
    public boolean draw;

    public Tag(double lat, double lng, String text) {
        this.lat = lat;
        this.lng = lng;
        this.text = text;
        this.angle = 0;
        this.distance = 0;
        this.x = 0;
        this.y = 0;
        this.draw = false;
    }
}
