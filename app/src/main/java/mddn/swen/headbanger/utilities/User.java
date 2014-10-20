package mddn.swen.headbanger.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.application.MainApplication;

/**
 * A utility that controls the current user state.
 *
 * Created by John on 18/10/2014.
 */
public class User {

    /**
     * Interface listener for when a user's profile picture becomes available
     */
    public interface ProfilePicListener {
        public void onPicLoaded(Bitmap profilePic);
    }

    /**
     * Listener for when the user successfully logs in
     */
    public interface UserLoggedInListener {
        public void onLogin(boolean loggedIn, GraphUser user);
    }

    /**
     * The Facebook graph user
     */
    private static GraphUser user;

    /**
     * When the user logs in, this is immediately fetched and referenced.
     *
     * External callers access this by assigning themselves as a
     * {@link mddn.swen.headbanger.utilities.User.ProfilePicListener}.
     */
    public static Bitmap profilePicture;

    /**
     * Called when the application is starting or resuming from a background state.
     *
     * Check to see if any user object exists - if one does, attempt to reauth with the server.
     */
    public static void resume() {
        if (Session.getActiveSession() == null) {
            Session.openActiveSessionFromCache(MainApplication.application.getApplicationContext());
        }
        if (isOpenFBSessionAvailable()) {
            requestUser(null, null);
        }
        else {
            logout();
        }
    }

    /**
     * Lazy mans check to see if there is a currently open Facebook Session to use
     *
     * @return True if a FB session is available
     */
    public static boolean isOpenFBSessionAvailable() {
        return Session.getActiveSession() != null && Session.getActiveSession().isOpened();
    }

    /**
     * To be called once Facebook returns, checks the current login state.
     *
     * @param context       Context to execute under
     * @param loginListener Listener for when the login process completes. May be null.
     */
    public static void login(Context context, UserLoggedInListener loginListener) {
        if (isOpenFBSessionAvailable()) {
            requestUser(context, loginListener);
        }
        else {
            if (loginListener != null) {
                loginListener.onLogin(false, null);
            }
            facebookLoginFailureDialog(context);
        }
    }

    /**
     * Clears the current user instance
     */
    public static void logout() {
        User.user = null;
        User.profilePicture = null;
        Session activeSession = Session.getActiveSession();
        if (activeSession == null) {
            activeSession = new Session(MainApplication.application.getApplicationContext());
            Session.setActiveSession(activeSession);
        }
        activeSession.closeAndClearTokenInformation();
    }

    /**
     * Access for the current {@link com.facebook.model.GraphUser}
     *
     * @return The current user, or null if none available.
     */
    public static GraphUser getGraphUser() {
        return user;
    }

    /**
     * Convenience method to determine if the user is presently logged in or not
     *
     * @return True if a {@link mddn.swen.headbanger.utilities.User#getGraphUser()} returns not null
     * or {@link mddn.swen.headbanger.utilities.User#isOpenFBSessionAvailable()} returns true.
     */
    public static boolean isLoggedIn() {
        return getGraphUser() != null || isOpenFBSessionAvailable();
    }

    /**
     * Only way to access the profile picture safely. Listener will be informed when the profile
     * picture becomes available - could be immediately.
     *
     * @param listener A listener wanting the profile picture of the current user.
     */
    public static void getProfilePicture(final ProfilePicListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (User.profilePicture == null) {
                        Thread.sleep(100);
                    }
                    if (listener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPicLoaded(User.profilePicture);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(User.class.toString(), e.toString());
                }
            }
        }).start();
    }

    /**
     * Attempts to load the user object for the current session instance. If no session exists this
     * will fail quietly.
     *
     * Providing a context will allow the app to display failure messages.
     *
     * @param context       The current context for displaying messages, may be null.
     * @param loginListener Listener for when the login process completes. May be null.
     */
    private static void requestUser(final Context context, final UserLoggedInListener loginListener) {
        if (isOpenFBSessionAvailable()) {
            Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    User.user = user;
                    if (user != null) {
                        if (loginListener != null) {
                            loginListener.onLogin(true, user);
                        }
                        loadUserProfilePicture();
                    } else {
                        if (loginListener != null) {
                            loginListener.onLogin(false, null);
                        }
                        facebookLoginFailureDialog(context);
                    }
                }
            }).executeAsync();
        }
    }

    /**
     * Display a Facebook login failure dialog for the current context
     *
     * @param context Context to display the dialog to
     */
    private static void facebookLoginFailureDialog(Context context) {
        logout();
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.facebook_failure_title))
                .setMessage(context.getString(R.string.facebook_failure_message))
                .setPositiveButton(context.getString(R.string.facebook_failure_dismiss_button), null)
                .show();
    }

    /**
     * Attempts to load the user's profile picture so we won't have to keep doing it.
     */
    private static void loadUserProfilePicture() {
        if (isLoggedIn()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String urlString = "https://graph.facebook.com/" +
                                User.user.getId() + "/picture?type=large";
                        URL url = new URL(urlString);
                        InputStream inputStream = url.openConnection().getInputStream();
                        BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
                        User.profilePicture = BitmapFactory.decodeStream(bufferedStream);
                    } catch (Exception e) {
                        Log.e(User.class.toString(), e.toString());
                    }
                }
            }).start();
        }
    }
}
