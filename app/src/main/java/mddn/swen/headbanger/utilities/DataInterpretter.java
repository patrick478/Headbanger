package mddn.swen.headbanger.utilities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Pragya on 22/10/14.
 */
public class DataInterpretter {

    private static final String TAG = DataInterpretter.class.getSimpleName();
    public static final String DATA_UPDATED = "mddn.swen.headbanger.action.DATA_UPDATED";

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
    private float maxRoll = 30;
    private float minRoll = -30;

    //previous read's values to compare with current read
    private GestureState prevPitchState;
    private float prevPitch;
    private GestureState prevRollState;
    private float prevRoll;

    private int gestureRange = 10;

    //Timing details for gestures
//    private TimerObject rollGestureTimer;  //TODO: figure out timer logic
    private float progress; //time tracking?    //FIXME: this doesn't seem to be used anywhere
    private int elapsedTime = 2;

    //state values for gestures
    private enum GestureState {INCREASING, DECREASING};

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

    /**
     * Get the current sensitivity preference
     *
     * @return
     */
    private int getCurrentSensitivity() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(parentActivity);
        return sp.getInt("current_sensitivity", 0);
    }


    public void interpretData(String data){
        /* The pipeline for processing gesture data */

        splitRawData(data);

        updateBoundaryValues();

        checkForGestures();

        broadcastData();

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
        } catch(Exception e){
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
            musicPlayer.addNod();
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
                gestureRange = (100-getCurrentSensitivity())/10 + 5;

                /* if user tilted or turned their head to the right, skip to the next song */
                if (musicPlayer.hasNextTrackLoaded() && roll < (0 - gestureRange)) {
                   //TODO: rollGestureTimer = new TimerObject; .. I think we need to restart the timer when switching to a new song
                    Log.d(TAG, "Skip to next track");
                    musicPlayer.skipToNext();
                }
                /* if user tilted or turned their head to the left, skip back to the previous song */
                else if (musicPlayer.hasPreviousTrackLoaded() && roll > (gestureRange)) {
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

    /**
     * The interpreter needs to broadcast the data everytime it is updated
     * The connected device fragment then recieves and updates icon and textfields
     *
     */
    private void broadcastData() {
        Intent i = new Intent(DATA_UPDATED);
        i.putExtra("pitch",pitch);
        i.putExtra("roll",roll);
        musicPlayer.sendBroadcast(i);
    }

    private void persistCurrentData() {
        prevPitch = pitch;
        prevPitchState = pitchState;
        prevRoll = roll;
        prevRollState = rollState;
    }

}
