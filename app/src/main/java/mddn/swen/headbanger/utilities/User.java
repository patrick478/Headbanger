package mddn.swen.headbanger.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import java.io.InputStream;
import java.net.URL;

import mddn.swen.headbanger.R;

/**
 * A utility that control the current user state and authentication.
 *
 * Created by John on 18/10/2014.
 */
public class User {

    /**
     * The Facebook graph user
     */
    private static GraphUser user;

    /**
     * When the user logs in, this is immediately fetched and referenced. If it has not
     * yet loaded set a profile availability listener. TODO need a listener...
     */
    public static Bitmap profilePicture;

    /**
     * Called by {@link mddn.swen.headbanger.application.MainApplication} when the application
     * is starting or resuming from a background state.
     *
     * Check to see if any user object exists - if one does, attempt to reauth with the server.
     */
    public static void resume() {
        if (isOpenSessionAvailable()) {
            requestUser(null); //Silently fail
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
    public static boolean isOpenSessionAvailable() {
        return Session.getActiveSession() != null && Session.getActiveSession().isOpened();
    }

    /**
     * To be called once Facebook returns, checks the current login state.
     */
    public static void facebookDidReturn(Context context) {
        if (isOpenSessionAvailable()) {
            requestUser(context);
        }
        else {
            facebookLoginFailureDialog(context);
        }
    }

    /**
     * Clears the current user instance
     */
    public static void logout() {
        User.user = null;
        User.profilePicture = null;
        if (Session.getActiveSession() != null) { //We don't care if its open or not, just kill it
            Session.getActiveSession().closeAndClearTokenInformation();
        }
    }

    /**
     * Attempts to load the user object for the current session instance. If no session exists this
     * will fail quietly.
     *
     * Providing a context will allow the app to display failure messages.
     *
     * @param context The current context for displaying messages, may be null.
     */
    private static void requestUser(final Context context) {
        if (isOpenSessionAvailable()) {
            Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    User.user = user;
                    if (user != null) {
                        loadUserProfilePicture();
                    } else {
                        facebookLoginFailureDialog(context);
                    }
                }
            }).executeAsync();
        }
    }

    /**
     * Display a Facebook login failure dialog
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
        if (User.user != null) {
            try {
                String imageURL;
                imageURL = "http://graph.facebook.com/" + User.user.getId() + "/picture?type=large";
                InputStream in = (InputStream) new URL(imageURL).getContent();
                User.profilePicture = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
            }
        }
    }
}
