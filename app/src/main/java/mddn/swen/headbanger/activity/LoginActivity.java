package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.fragment.DeviceSelectorFragment;
import mddn.swen.headbanger.fragment.LoginFragment;

public class LoginActivity extends Activity {

    /**
     * The selector fragment
     */
    LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            loginFragment = (LoginFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment_login);
        }
    }
}
