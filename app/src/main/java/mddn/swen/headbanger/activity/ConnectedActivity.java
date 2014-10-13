package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.os.Bundle;
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
}
