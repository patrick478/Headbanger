package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
public class ConnectedDeviceFragment extends Fragment implements SensorEventListener{

    private static final String TAG = ConnectedDeviceFragment.class.getSimpleName();


    private Float pitch;
    private Float roll;

    private ImageView connectedIcon;
    private TextView songName;
    private TextView songArtist;
    private TextView songAlbum;
    private TextView nodCountDisplay;
    private ImageView playStatusDisplay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected, container, false);
        ButterKnife.inject(this, view);
        Context context = this.getActivity();

        connectedIcon = (ImageView) view.findViewById(R.id.headbanger_connected_icon);
        songName = (TextView) view.findViewById(R.id.song_title);
        songArtist = (TextView) view.findViewById(R.id.song_artist);
        songAlbum = (TextView) view.findViewById(R.id.song_album);
        nodCountDisplay = (TextView) view.findViewById(R.id.nod_count_value);
        playStatusDisplay = (ImageView) view.findViewById(R.id.play_status_image);

        setPlayIcon();

        IntentFilter iF = new IntentFilter();
        iF.addAction(DataInterpretter.DATA_UPDATED);
        iF.addAction(MusicPlayerActivity.SERVICECMD);

        this.getActivity().registerReceiver(mReceiver, iF);

        SensorManager manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        if(manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
                Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
                manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            Log.i("","Sensor listener registered");
            }

        beginIconWiggle();
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
                    setPlayIcon();
                }
                else if (command.equals(MusicPlayerActivity.CMDPAUSE)) {
                    setPauseIcon();
                }
            }
        }
    };

    private void setPauseIcon(){
        playStatusDisplay.setBackgroundResource(R.drawable.ic_pause);

    }

    private void setPlayIcon(){
        playStatusDisplay.setBackgroundResource(R.drawable.ic_play);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        connectedIcon.clearAnimation();
        ButterKnife.reset(this);
    }

    /**
     * Begins the wiggle animation for the headbanger icon
     */
    private void beginIconWiggle() {
        Animation wiggle = AnimationUtils.loadAnimation(this.getActivity(),
                R.anim.connected_icon_wiggle_anim);
        wiggle.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(final Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (connectedIcon != null) {
                            connectedIcon.clearAnimation();
                            beginIconWiggle();
                        }
                    }
                }, 5000);
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        connectedIcon.startAnimation(wiggle);
    }

    /**
     * Methods for Sensor wont be needed once the
     * @param sensor
     * @param accuracy
     */
    @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if(connectedIcon != null) {
                /**
                 * Get the pitch values from the control activity
                 */
//                pitchIcon(event.values[0]/20);
//                Log.i("Pitch",Float.toString(event.values[0]));
//                rollIcon(event.values[1]*10);
//                Log.i("Roll",Float.toString(event.values[1]));

            }
        }

    /**
     * Rotates the icon depending on values from the headphones/Sensor for testing
     */
    private void rollIcon(float roll) {
        connectedIcon.setRotation(roll*2);
    }

    private void pitchIcon(float pitch) {
        connectedIcon.setScaleY(Math.abs(1 - pitch/100));
    }



}
