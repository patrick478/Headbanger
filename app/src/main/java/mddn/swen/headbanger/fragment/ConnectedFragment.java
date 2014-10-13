package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;

/**
 * Is displayed when the application has successfully connected with a device
 */
public class ConnectedFragment extends Fragment {

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
        connectedIcon.setAnimation(null);
        ButterKnife.reset(this);
    }

    /**
     * Begins the wiggle animation for the headbanger icon
     */
    private void beginIconWiggle() {
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        connectedIcon.startAnimation(anim);
    }
}
