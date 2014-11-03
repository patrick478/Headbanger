package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.activity.DeviceControlActivity;
import mddn.swen.headbanger.utilities.DataInterpretter;
import mddn.swen.headbanger.utilities.MusicPlayerActivity;

/**
 * Is displayed when the application has successfully connected with a device
 */
public class ConnectedDeviceFragment extends Fragment{

    private static final String TAG = ConnectedDeviceFragment.class.getSimpleName();

    private Float pitch;
    private Float roll;
    private int nodCount;

    private ImageView connectedIcon;
    private View headphoneBackground;
    private TextView songName;
    private TextView songArtist;
    private TextView songAlbum;
    private TextView nodCountDisplay;
    private ImageView playStatusDisplay;
    private ImageView previousStatusDisplay;
    private ImageView nextStatusDisplay;

    private String artist;
    private String track;
    private String album;

    private IntentFilter iF;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected, container, false);
        ButterKnife.inject(this, view);
        Context context = this.getActivity();

        connectedIcon = (ImageView) view.findViewById(R.id.headbanger_connected_icon);
        songName = (TextView) view.findViewById(R.id.song_title);
        songArtist = (TextView) view.findViewById(R.id.song_artist);
        songAlbum = (TextView) view.findViewById(R.id.song_album);
        nodCountDisplay = (TextView) view.findViewById(R.id.nod_count_header);
        playStatusDisplay = (ImageView) view.findViewById(R.id.play_status_image);
        previousStatusDisplay = (ImageView) view.findViewById(R.id.previous_status_image);
        nextStatusDisplay = (ImageView) view.findViewById(R.id.next_status_image);

        iF = new IntentFilter();
        iF.addAction(DataInterpretter.DATA_UPDATED);
        iF.addAction(MusicPlayerActivity.SERVICECMD);
        iF.addAction(MusicPlayerActivity.META_CHANGED);
        iF.addAction(MusicPlayerActivity.NOD_CHANGED);

        this.getActivity().registerReceiver(mReceiver, iF);

        beginTutorialAnim();

        return view;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* Receive pitch and roll data updates to update the headphones animation */
            String action = intent.getAction();
            Log.d(TAG, action);

            if (action.equals(DataInterpretter.DATA_UPDATED)){
                pitch = intent.getFloatExtra("pitch", 0);
                roll = intent.getFloatExtra("roll",0);
                pitchIcon(pitch);
                rollIcon(roll);
            }
            else if (action.equals(MusicPlayerActivity.SERVICECMD)) {
                String command = intent.getStringExtra(MusicPlayerActivity.CMDNAME);
                if (command.equals(MusicPlayerActivity.CMDPLAY)) {
                    setPauseIcon();
                }
                else if (command.equals(MusicPlayerActivity.CMDPAUSE)) {
                    setPlayIcon();
                }
                else if (command.equals(MusicPlayerActivity.CMDNEXT)){
                    animateGesture(nextStatusDisplay);
                    Animation slide = AnimationUtils.loadAnimation(ConnectedDeviceFragment.this.getActivity(), R.anim.track_info_anim);
                    songName.startAnimation(slide);
                    songArtist.startAnimation(slide);
                    songAlbum.startAnimation(slide);
                    showRating(nodCount);

                }
                else if (command.equals(MusicPlayerActivity.CMDPREVIOUS)){
                    animateGesture(previousStatusDisplay);
                    Animation slide = AnimationUtils.loadAnimation(ConnectedDeviceFragment.this.getActivity(), R.anim.track_info_reverse_anim);
                    songName.startAnimation(slide);
                    songArtist.startAnimation(slide);
                    songAlbum.startAnimation(slide);
                    showRating(nodCount);
                }
            }
            else if (action.equals(MusicPlayerActivity.META_CHANGED)){
                artist = intent.getStringExtra("artist");
                album = intent.getStringExtra("album");
                track = intent.getStringExtra("track");

                songName.setText(track);
                songArtist.setText(artist);
                songAlbum.setText(album);
                showRating(nodCount);
            }else if (action.equals(MusicPlayerActivity.NOD_CHANGED)){
                nodCount = intent.getIntExtra(MusicPlayerActivity.NOD_CHANGED, 0);
            }
        }
    };

    private void setPauseIcon(){
        playStatusDisplay.setBackgroundResource(R.drawable.pause);

    }

    private void setPlayIcon(){
        playStatusDisplay.setBackgroundResource(R.drawable.play);
    }

    private void showRating(int nodCount){
        nodCountDisplay.setText(String.format("Rating: %d", nodCount));
        nodCountDisplay.setVisibility(View.VISIBLE);
        Animation rating = AnimationUtils.loadAnimation(ConnectedDeviceFragment.this.getActivity(), R.anim.ratings_anim);
        nodCountDisplay.startAnimation(rating);
    }

    private void animateGesture(ImageView view){
        Animation gesture = AnimationUtils.loadAnimation(ConnectedDeviceFragment.this.getActivity(), R.anim.gesture_anim);
        view.startAnimation(gesture);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        connectedIcon.clearAnimation();
        playStatusDisplay.clearAnimation();
        nextStatusDisplay.clearAnimation();
        previousStatusDisplay.clearAnimation();
        nodCountDisplay.clearAnimation();
        ButterKnife.reset(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        this.getActivity().registerReceiver(mReceiver,iF);
    }

    /**
     * Begins the wiggle animation for the headbanger icon
     */
    private void beginTutorialAnim() {
        Animation tutorialIcon = AnimationUtils.loadAnimation(this.getActivity(), R.anim.tutorial_headphone_anim);
        connectedIcon.startAnimation(tutorialIcon);

        Animation nextGesture = AnimationUtils.loadAnimation(ConnectedDeviceFragment.this.getActivity(), R.anim.tutorial_gesture_next_anim);
        nextStatusDisplay.startAnimation(nextGesture);
        Animation prevGesture = AnimationUtils.loadAnimation(ConnectedDeviceFragment.this.getActivity(), R.anim.tutorial_gesture_anim);
        previousStatusDisplay.startAnimation(prevGesture);
    }


    /**
     * Rotates the icon depending on values from the headphones/Sensor for testing
     */
    private void rollIcon(float roll) {
        connectedIcon.setRotation(-roll*2);
    }

    private void pitchIcon(float pitch) {
        connectedIcon.setScaleY(1 - Math.abs(pitch/100));
    }



}
