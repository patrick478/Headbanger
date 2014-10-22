package mddn.swen.headbanger.application;

import android.app.Application;

import mddn.swen.headbanger.utilities.User;

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
}
