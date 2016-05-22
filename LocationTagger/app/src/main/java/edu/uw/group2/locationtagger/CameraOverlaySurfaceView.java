package edu.uw.group2.locationtagger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alexb on 5/20/2016.
 */
public class CameraOverlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "SurfaceView";
    private static final double MAX_VEIWING_DISTANCE = .5;
    private static final int ANGLE_DIFFERENCE = 50;

    private int viewWidth, viewHeight; //size of the view

    private Bitmap bmp; //image to draw on


    private SurfaceHolder mHolder; //the holder we're going to post updates to
    private DrawingRunnable mRunnable; //the code that we'll want to run on a background thread
    private Thread mThread; //the background thread

    private Paint whitePaint; //drawing variables (pre-defined for speed)


    private Tag testTag = new Tag(47.659465,  -122.319791, "Test Point"); //Just a test point
    private Tag testTagTwo = new Tag(47.660179,  -122.319777, "Test Point 2"); //Just a test point
    public ArrayList<Tag> allTags;
    public Tag currentLocation;
    public boolean curLocationChanged;

    public double rotation;
    private double oldRotation;
    public double roll;
    public double pitch;


    /**
     * We need to override all the constructors, since we don't know which will be called
     */
    public CameraOverlaySurfaceView(Context context) {
        this(context, null);
    }

    public CameraOverlaySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraOverlaySurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        viewWidth = 1; viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();

        //set up drawing variables ahead of timme
        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(80);

        oldRotation = 200;

        allTags = new ArrayList<>();
        allTags.add(testTag);
        allTags.add(testTagTwo);
    }


    public void init() {

    }

    public void changeCurrentLocation(Tag newCurrent) {
        currentLocation = newCurrent;
        curLocationChanged = true;
    }

    /**
     * Update where the text should appear on the screen
     */
    public void update(){
        for (Tag tag : (ArrayList<Tag>) allTags.clone()) {
            if (currentLocation != null && curLocationChanged) {
                //get distance
                double currToTag = distanceBetweenTags(currentLocation, tag);
                tag.distance = currToTag;

                //only calculate if we are drawing it
                if (tag.distance <= MAX_VEIWING_DISTANCE) {
                    //create triangle to get distances and angles
                    Tag helperTag = new Tag(currentLocation.lat + 1, currentLocation.lng, "");
                    double northLine = distanceBetweenTags(currentLocation, helperTag);
                    double helpToTag = distanceBetweenTags(helperTag, tag);

                    //find angle
                    double angleInDegree = radToDeg(Math.acos((northLine * northLine + currToTag * currToTag - helpToTag * helpToTag) / (2 * northLine * currToTag)));

                    //shift angle to android version if needed
                    if (angleInDegree > 180) {
                        angleInDegree = angleInDegree - 360;
                    }

                    //save
                    tag.angle = angleInDegree;
                }
                curLocationChanged = false;
            }

            //update everytime
            //get y position
            tag.y = viewHeight / 2;

            //get x position
            double rotDiff = Math.abs(rotation - oldRotation);
            if (rotDiff > 1) {//has rotation changed enough to update x
                if (rotation >= 0) {
                    tag.x = ((Double) (((-(tag.angle - Math.round(rotation)) * 20 + (viewWidth / 2)) + tag.x) / 2)).intValue();
                } else {
                    tag.x = ((Double) ((((tag.angle + Math.round(rotation)) * 20 + (viewWidth / 2)) + tag.x) / 2)).intValue();
                }
            }

            tag.draw = (tag.distance <= MAX_VEIWING_DISTANCE && tag.x > -500 && tag.x < viewWidth);
        }
        oldRotation = rotation;
    }

    /**
     * Return distance between two points in miles
     * @param one
     * @param two
     * @return
     */
    public double distanceBetweenTags(Tag one, Tag two) {
        double lon1 = one.lng;
        double lat1 = one.lat;
        double lon2 = two.lng;
        double lat2 = two.lat;

        double theta = lon1 - lon2;
        double dist = Math.sin(degToRad(lat1)) * Math.sin(degToRad(lat2)) + Math.cos(degToRad(lat1)) * Math.cos(degToRad(lat2)) * Math.cos(degToRad(theta));
        dist = Math.acos(dist);
        dist = radToDeg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    public double degToRad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double radToDeg(double rad) {
        return (rad * 180 / Math.PI);
    }

    /**
     * Helper method for the "render loop"
     * @param canvas The canvas to draw on
     */
    public void render(Canvas canvas) {
        //Log.v(TAG, "Render Loop");
        if (canvas == null) return; //if we didn't get a valid canvas for whatever reason
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawText("Rotation: " + rotation, 0, 80, whitePaint);
        canvas.drawText("Test Tag Angle: " + testTag.angle, 0, 160, whitePaint);

        for (Tag tag : (ArrayList<Tag>)allTags.clone()) {
            if (tag.draw) {
                canvas.drawText(tag.text, tag.x, tag.y, whitePaint);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //create and start the background updating thread
        Log.d(TAG, "Creating new drawing thread");
        mThread = new Thread(mRunnable);
        mRunnable.setRunning(true); //turn on the runner
        mThread.start(); //start up the thread when surface is created

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mHolder) { //synchronized to keep this stuff atomic
            viewWidth = width;
            viewHeight = height;
            bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); //new buffer to draw on

            init();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        mRunnable.setRunning(false); //turn off
        boolean retry = true;
        while(retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //will try again...
            }
        }
        Log.d(TAG, "Drawing thread shut down");
    }


    /**
     * An inner class representing a runnable that does the drawing. Animation timing could go in here.
     * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
     */
    public class DrawingRunnable implements Runnable {

        private boolean isRunning; //whether we're running or not (so we can "stop" the thread)

        public void setRunning(boolean running){
            this.isRunning = running;
        }

        public void run() {
            Canvas canvas;
            while(isRunning)
            {
                canvas = null;
                try {
                    canvas = mHolder.lockCanvas(); //grab the current canvas
                    synchronized (mHolder) {
                        update(); //update the game
                        render(canvas); //redraw the screen
                    }
                }
                finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
