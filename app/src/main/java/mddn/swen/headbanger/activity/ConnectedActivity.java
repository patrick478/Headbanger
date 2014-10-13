package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.fragment.ConnectedFragment;
import mddn.swen.headbanger.fragment.DeviceSelectorFragment;

public class ConnectedActivity extends Activity {

    /**
     * The fragment for this activity
     */
    private ConnectedFragment connectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
        if (savedInstanceState == null) {
            connectedFragment = (ConnectedFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment_connected);
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
