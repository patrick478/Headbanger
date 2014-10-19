package mddn.swen.headbanger.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import mddn.swen.headbanger.BuildConfig;
import mddn.swen.headbanger.R;

import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.*;

public class UserSettingsController {

    /* Context to build under */
    private Context context;

    /**
     * Used to enumerate the type of Preference to be displayed
     */
    public static enum HeadbangerPreference {

        /* List the preferences */
        EDITTEXT_DISPLAY_NAME(R.string.settings_pref_display_name, "display_name"),
        BUTTON_SIGN_OUT(R.string.settings_pref_sign_out, null),
        BUTTON_BUILD(R.string.settings_pref_build, null);

        /* The resource ID of the item */
        final int resId;

        /* The current value of the property */
        final String property;

        HeadbangerPreference(int resId, String property) {
            this.resId = resId;
            this.property = property;
        }
    }

    /**
     * Build the instance of the settings controller.
     *
     * @param context   The context to build for
     * @param screen    The preference screen to attach to
     */
    public UserSettingsController(Context context, PreferenceScreen screen) {
        this.context = context;
        addGeneralInfo(screen, context);
        addManage(screen, context);
        addAbout(screen, context);
    }

    /**
     * Adds the general information section
     *
     * @param context   The context to build for
     * @param screen    The preference screen to attach to
     */
    private void addGeneralInfo(PreferenceScreen screen, Context context) {
        PreferenceCategory generalCategory = new PreferenceCategory(context);
        screen.addPreference(generalCategory);
        generalCategory.setTitle(R.string.settings_cat_general);
        addButton(generalCategory, context, EDITTEXT_DISPLAY_NAME, User.getGraphUser().getName());
    }

    /**
     * Adds the account management section
     *
     * @param context   The context to build for
     * @param screen    The preference screen to attach to
     */
    private void addManage(PreferenceScreen screen, Context context) {
        PreferenceCategory manageCategory = new PreferenceCategory(context);
        screen.addPreference(manageCategory);
        manageCategory.setTitle(R.string.settings_cat_manage);
        addButton(manageCategory, context, BUTTON_SIGN_OUT, null);
    }

    /**
     * Adds the app about section
     *
     * @param context   The context to build for
     * @param screen    The preference screen to attach to
     */
    private void addAbout(PreferenceScreen screen, Context context) {
        PreferenceCategory aboutCategory = new PreferenceCategory(context);
        screen.addPreference(aboutCategory);
        aboutCategory.setTitle(R.string.settings_cat_about);
        addButton(aboutCategory, context, BUTTON_BUILD, getBuildString());
    }

    /**
     * Helper function to add a button to the preferences pane
     *
     * @param parent  The group this will belong to
     * @param context The context to build under
     * @param pref    The preference this is building for
     * @param value   The current value of the preference
     */
    private void addButton(PreferenceGroup parent, Context context, final HeadbangerPreference pref,
                           String value) {
        Preference preference = new Preference(context);
        preference.setTitle(pref.resId);
        if (value != null) {
            preference.setSummary(value);
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onButtonClick(pref);
                return true;
            }
        });
        parent.addPreference(preference);
    }

    /**
     * Helper that will respond to button presses
     *
     * @param preference The preference button that was tapped
     */
    private void onButtonClick(HeadbangerPreference preference) {
        switch (preference) {
            case EDITTEXT_DISPLAY_NAME:
                showEditDisplayName();
                break;
            case BUTTON_SIGN_OUT:
                showSignOutConfirmation();
                break;
            default:
                break;
        }
    }

    /**
     * Displays an input field that allows the user to modify their display name
     */
    private void showEditDisplayName() {

    }

    /**
     * Called when the user elects to sign out
     */
    private void showSignOutConfirmation() {
        if (context == null) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(R.string.settings_confirm_sign_out_title)
                .setMessage(R.string.settings_confirm_sign_out_descriptor)
                .setCancelable(true)
                .setPositiveButton(R.string.settings_confirm_sign_out_positive,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                User.logout();
                            }
                        })
                .setNegativeButton(R.string.settings_confirm_sign_out_negative, null)
                .show();
    }

    /**
     * Returns the build information for the app
     *
     * @return A string with the build information
     */
    private String getBuildString() {
        return BuildConfig.VERSION_NAME
                + " " + BuildConfig.BUILD_TYPE + " (code " + BuildConfig.VERSION_CODE + ")"
                + ", API: " + Build.VERSION.SDK_INT
                + ", MODEL: "+ Build.MODEL
                + ", PRODUCT: " + Build.PRODUCT
                + ", OS BUILD: " + Build.ID;
    }
}
