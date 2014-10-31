package mddn.swen.headbanger.utilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.activity.BluetoothLeService;
import mddn.swen.headbanger.navigation.NavigationDrawerFragment;

/**
 * Created by Pragya on 22/10/14.
 */
public class MusicPlayerActivity extends Activity{

    private static final String TAG = MusicPlayerActivity.class.getSimpleName();

    private Handler mHandler;

    /* music player states for gesture interpretation */
//    private boolean playing;
    private boolean nextLoaded = true;
    private boolean previousLoaded = true;
    private int nodCount = 0;

    /* Music control fields */
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPLAY = "play";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";

    /* Music information */
    public String track;
    public String album;
    public String artist;
    public Boolean playing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* set up audio playback control */
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");

        registerReceiver(mReceiver, iF);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* Interpret audio control action */
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.v("tag ", action + " / " + cmd);
            artist = intent.getStringExtra("artist");
            album = intent.getStringExtra("album");
            track = intent.getStringExtra("track");
            playing = intent.getBooleanExtra("playing",false);
            String nowPlayingInfo = album +"\n"+ track +"\n"+ artist + "\n" + playing;
            Log.d(TAG, nowPlayingInfo);
//            resetNodCount();
        }
    };

    //TODO: get notifications of audio track changes so that the nod count can be reset for each track


    public void skipToNext() {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDNEXT);
        MusicPlayerActivity.this.sendBroadcast(i);
        Log.i(CMDNAME, CMDNEXT);
    }

    public void skipToPrevious() {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDPREVIOUS);
        MusicPlayerActivity.this.sendBroadcast(i);
        Log.i(CMDNAME, CMDPREVIOUS);
    }

    public void pauseMusic() {
        if(playing){
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDPAUSE);
            MusicPlayerActivity.this.sendBroadcast(i);
            Log.i(CMDNAME, CMDPAUSE);
            playing = false;
        }
    }

    public void playMusic() {
        if(!playing){
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDPLAY);
            MusicPlayerActivity.this.sendBroadcast(i);
            Log.i(CMDNAME, CMDPLAY);
            playing = true;
        }
    }

    public void addNod(){
        nodCount++;
//        nodCountDisplay.setText("" + nodCount);
        Log.d(TAG, "nod count increased to " + nodCount);
    }

    public void resetNodCount(){
        nodCount = 0;
//        nodCountDisplay.setText(String.format("%d", nodCount));
        Log.d(TAG, "nod count reset to 0");
    }

    public boolean isPaused() {
        return !playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean hasPreviousTrackLoaded() {
        return previousLoaded;
    }

    public boolean hasNextTrackLoaded() {
        return nextLoaded;
    }
}
