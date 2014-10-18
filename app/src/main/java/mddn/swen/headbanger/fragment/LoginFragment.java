package mddn.swen.headbanger.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.utilities.User;

/**
 * Fragment responsible for displaying a UI to the user which allows them to login in to the
 * application.
 */
public class LoginFragment extends Fragment {

    /**
     * Reference the image view
     */
    @InjectView(R.id.headbanger_login_icon)
    ImageView headbangerIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);
        User.getProfilePicture(new User.ProfilePicListener() {
            @Override
            public void onPicLoaded(Bitmap profilePic) {
                headbangerIcon.setImageBitmap(profilePic);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
