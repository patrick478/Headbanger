package mddn.swen.headbanger.application;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import mddn.swen.headbanger.utilities.User;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Main application entry point
 *
 * Created by John on 9/10/2014.
 */
public class MainApplication extends Application {

    /**
     * Publically available instance of the application
     */
    public static MainApplication application;

    /**
     * The entry point for the application's lifecycle
     */
    public MainApplication() {
        super();
        application = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        User.resume();
    }

    public void onResume() {
        User.resume();
    }
}
