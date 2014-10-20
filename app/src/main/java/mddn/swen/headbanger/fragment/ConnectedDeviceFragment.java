package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;

/**
 * Is displayed when the application has successfully connected with a device
 */
public class ConnectedDeviceFragment extends Fragment {

    /* The headphones icon */
    @InjectView(R.id.headbanger_connected_icon)
    ImageView connectedIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected, container, false);
        ButterKnife.inject(this, view);
        beginIconWiggle();
        return view;
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


}
