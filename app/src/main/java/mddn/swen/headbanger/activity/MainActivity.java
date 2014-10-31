package mddn.swen.headbanger.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;

import java.security.MessageDigest;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.navigation.NavigationDrawerFragment;
import mddn.swen.headbanger.utilities.User;

/**
 * The root activity for the app that holds the navigation drawer and swaps its fragments out
 * each time a screen is "changed"
 */
public class MainActivity extends DeviceControlActivity {

    /**
     * The drawer layout containing both views
     */
    @InjectView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
        ButterKnife.inject(this);
        navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
        navigationDrawerFragment.selectItem(0);
        navigationDrawerFragment.checkIfUserLearnedDrawer();


        /* set up signatures for Facebook login */
        try {
            PackageInfo info = getPackageManager().getPackageInfo("mddn.swen.headbanger", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch(Exception e) {}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                navigationDrawerFragment.openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User.resume();
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE) {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
            System.out.println("Session: " + Session.getActiveSession());
            User.login(this, new User.UserLoggedInListener() {
                @Override
                public void onLogin(boolean loggedIn, GraphUser user) {
                    if (loggedIn) {
                        navigationDrawerFragment.refreshFragment();
                    }
                }
            });
        }
    }

    /**
     * Returns the current navigation drawer fragment
     *
     * @return The {@link mddn.swen.headbanger.navigation.NavigationDrawerFragment} for this
     * instance.
     */
    public NavigationDrawerFragment getDrawer() {
        return navigationDrawerFragment;
    }

}
