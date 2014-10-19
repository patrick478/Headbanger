package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;

import com.facebook.Session;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.navigation.NavigationDrawerFragment;
import mddn.swen.headbanger.utilities.User;

/**
 * The root activity for the app that holds the navigation drawer and swaps its fragments out
 * each time a screen is "changed"
 */
public class MainActivity extends Activity {

    /**
     * The drawer layout containing both views
     */
    @InjectView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    protected NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
        ButterKnife.inject(this);
        navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
        navigationDrawerFragment.selectItem(0);
        navigationDrawerFragment.checkIfUserLearnedDrawer();
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
            User.login(this);
        }
    }

    /**
     * Some psuedo-code on how to send a "touch" event.
     *
     * Supposedly works on Google music, untested Spotify
     *
     * http://stackoverflow.com/questions/2659148/possible-to-skip-track-from-an-android-application
     *
     * Apps can ignore this event potentially, would be nice if Spotify didn't but I bet they do.
     */
    public void onSomeFunkyBTInput() {
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        synchronized (this) {
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
            sendOrderedBroadcast(i, null);

            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
            sendOrderedBroadcast(i, null);
        }
    }
}
