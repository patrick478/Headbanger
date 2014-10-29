package mddn.swen.headbanger.utilities;

import android.app.Activity;
import android.util.Log;

/**
 * Created by Pragya on 22/10/14.
 */
public class DataInterpretter {

    private static final String TAG = DataInterpretter.class.getSimpleName();

    //raw data from read
    private String rawData;

    //values to be derived from read data
    private float pitch;
    private float roll;
    private float yaw;

    private GestureState pitchState;
    private GestureState rollState;

    //minimum and maximum boundary values
    private float maxPitch = 0;
    private float minPitch = 0;
    private float maxRoll;
    private float minRoll;

    //previous read's values to compare with current read
    private GestureState prevPitchState;
    private float prevPitch;
    private GestureState prevRollState;
    private float prevRoll;

//    private int gestureRange = 15;   //FIXME: this is used, but not sure what it is or what value it should have.

    //Timing details for gestures
//    private TimerObject rollGestureTimer;  //TODO: figure out timer logic
    private float progress; //time tracking?    //FIXME: this doesn't seem to be used anywhere
    private int elapsedTime = 2;

    //state values for gestures
    private enum GestureState {INCREASING, DECREASING};

    private String nodBPM;  //FIXME: not sure what this is for

    //rating data
    private int nodCount = 0;

    //music playback controller
    private MusicPlayerActivity musicPlayer;

    Activity parentActivity;


    /**
     * constructor to initialise values
     */
    public DataInterpretter(Activity parent){
        parentActivity = parent;
        musicPlayer = (MusicPlayerActivity) parent;
    }



    public void interpretData(String data){
        /* The pipeline for processing gesture data */

        splitRawData(data);

        updateBoundaryValues();

        checkForGestures();

        /* save current gesture data to compare with data from the next read */
        persistCurrentData();
    }

    private void splitRawData(String data){
        //TODO: split data into four segments: pitch, roll, yaw, yawU

        String[] dataArray = data.split(",");

        try {
            pitch = Float.parseFloat(dataArray[0]);
            roll = Float.parseFloat(dataArray[1]);
            Log.d(TAG, "pitch is: " + pitch);
            Log.d(TAG, "roll is: " + roll);

            //yaw = newYaw
        } catch(NumberFormatException e){
            e.printStackTrace();
        }
    }

    private void updateBoundaryValues(){
        if (pitch > maxPitch){
            maxPitch = pitch;
        }
        if (pitch < minPitch){
            minPitch = pitch;
        }
        if (roll > maxRoll){
            maxRoll = roll;
        }
        if (roll < minRoll){
            minRoll = roll;
        }
    }

    private void checkForGestures(){

        /* check whether the user has nodded their head (up/down motion) */
        checkNod();

        /* check whether the user has tilted or turned  their head to the left or right */
        checkTilt();

        /* check whether the user has taken the headphones off or put them back on */
        checkOnOrOff();
    }

    private void checkNod() {
        /* use pitch data to determine whether the user has nodded their head */
        pitchState = prevPitchState;

        /* if the user has tilted their head back or forward, note the pitch state as increasing or decreasing */
        if (pitch > prevPitch){
            pitchState = GestureState.INCREASING;
        }
        else if (pitch < prevPitch){
            pitchState = GestureState.DECREASING;
        }

        /* If a nod has happened, make sure the music is on (because we know that the headphone are on the user's head),
         * and increment the nod count */
        if (pitchState != prevPitchState && musicPlayer.isPlaying()){
            nodCount++;
            Log.d(TAG, "nod count increased to " + nodCount);
        }
    }

    private void checkTilt() {
        rollState = prevRollState;

        if (roll > prevRoll){
            rollState = GestureState.INCREASING;
        }
        else if (roll < prevRoll){
            rollState = GestureState.DECREASING;
        }

        /* the gesture we are looking for only matters if music is not paused */
        if (musicPlayer.isPlaying()) {
            if (rollState != prevRollState && elapsedTime > 1) { //TODO: figure out timer logic

                /* if user tilted or turned their head to the right, skip to the next song */
                if (musicPlayer.hasNextTrackLoaded() && roll < (maxRoll - 5)) {
                   //TODO: rollGestureTimer = new TimerObject; .. I think we need to restart the timer when switching to a new song
                    Log.d(TAG, "Skip to next track");
                    musicPlayer.skipToNext();
                }
                /* if user tilted or turned their head to the left, skip back to the previous song */
                else if (musicPlayer.hasPreviousTrackLoaded() && roll > (minRoll + 5)) {
                    //TODO: rollGestureTimer = TimerObject; .. I think we need to restart the timer when switching to a new song
                    Log.d(TAG, "Skip to previous track");
                    musicPlayer.skipToPrevious();
                }
            }

        }
    }

    private void checkOnOrOff() {
        if (musicPlayer.isPlaying() && pitch > 18){
            musicPlayer.pauseMusic();
        }
        else if (musicPlayer.isPaused() && pitch < 10){
            musicPlayer.playMusic();
        }
    }

    private void persistCurrentData() {
        prevPitch = pitch;
        prevPitchState = pitchState;
        prevRoll = roll;
        prevRollState = rollState;
    }

    public void resetNodCount(){
        nodCount = 0;
    }

}
