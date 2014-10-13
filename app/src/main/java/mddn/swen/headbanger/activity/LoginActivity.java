package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
            Session.getActiveSession().closeAndClearTokenInformation(); //TODO don't include this for release
//            loginSuccessful(); //TODO do include this instead
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        checkLogin();
    }

    /**
     * Called once Facebook returns, checks the current login state
     */
    private void checkLogin() {
        Session currentSession = Session.getActiveSession();
        if (currentSession.isOpened()) {
            Request.newMeRequest(currentSession, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        System.out.println(user);
                        loginSuccessful();
                    }
                    else {
                        facebookLoginFailureDialog();
                    }
                }
            }).executeAsync();
        }
        else {
            facebookLoginFailureDialog();
        }
    }

    /**
     * Display a Facebook login failure dialog
     */
    private void facebookLoginFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.facebook_failure_title))
                .setMessage(getString(R.string.facebook_failure_message))
                .setPositiveButton(getString(R.string.facebook_failure_dismiss_button), null)
                .show();
    }

    /**
     * To be called when login was successfully achieved - begins the root activity
     */
    private void loginSuccessful() {
        startActivity(new Intent(this, RootActivity.class));
        finish();
    }
}
