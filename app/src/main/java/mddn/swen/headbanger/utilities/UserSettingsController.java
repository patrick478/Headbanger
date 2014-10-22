package mddn.swen.headbanger.utilities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import mddn.swen.headbanger.BuildConfig;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.activity.MainActivity;

import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.BUTTON_BUILD;
import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.BUTTON_SIGN_OUT;
import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.EDITTEXT_DISPLAY_NAME;

public class UserSettingsController {

    /* Context to build under */
    private MainActivity mainActivity;

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
     * @param mainActivity The context to build for
     * @param screen       The preference screen to attach to
     */
    public UserSettingsController(MainActivity mainActivity, PreferenceScreen screen) {
        this.mainActivity = mainActivity;
        addGeneralInfo(screen);
        addManage(screen);
        addAbout(screen);
    }

    /**
     * Adds the general information section
     *
     * @param screen    The preference screen to attach to
     */
    private void addGeneralInfo(PreferenceScreen screen) {
        PreferenceCategory generalCategory = new PreferenceCategory(mainActivity);
        screen.addPreference(generalCategory);
        generalCategory.setTitle(R.string.settings_cat_general);
        addButton(generalCategory, EDITTEXT_DISPLAY_NAME, User.getGraphUser().getName());
    }

    /**
     * Adds the account management section
     *
     * @param screen    The preference screen to attach to
     */
    private void addManage(PreferenceScreen screen) {
        PreferenceCategory manageCategory = new PreferenceCategory(mainActivity);
        screen.addPreference(manageCategory);
        manageCategory.setTitle(R.string.settings_cat_manage);
        addButton(manageCategory, BUTTON_SIGN_OUT, null);
    }

    /**
     * Adds the app about section
     *
     * @param screen    The preference screen to attach to
     */
    private void addAbout(PreferenceScreen screen) {
        PreferenceCategory aboutCategory = new PreferenceCategory(mainActivity);
        screen.addPreference(aboutCategory);
        aboutCategory.setTitle(R.string.settings_cat_about);
        addButton(aboutCategory, BUTTON_BUILD, getBuildString());
    }

    /**
     * Helper function to add a button to the preferences pane
     *
     * @param parent  The group this will belong to
     * @param pref    The preference this is building for
     * @param value   The current value of the preference
     */
    private void addButton(PreferenceGroup parent, final HeadbangerPreference pref, String value) {
        Preference preference = new Preference(mainActivity);
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
            case BUTTON_SIGN_OUT:
                showSignOutConfirmation();
                break;
            default:
                break;
        }
    }

    /**
     * Called when the user elects to sign out
     */
    private void showSignOutConfirmation() {
        if (mainActivity == null) {
            return;
        }
        new AlertDialog.Builder(mainActivity)
                .setTitle(R.string.settings_confirm_sign_out_title)
                .setMessage(R.string.settings_confirm_sign_out_descriptor)
                .setCancelable(true)
                .setPositiveButton(R.string.settings_confirm_sign_out_positive,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                User.logout();
                                mainActivity.getDrawer().refreshFragment();
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
