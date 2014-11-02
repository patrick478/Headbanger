package mddn.swen.headbanger.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import mddn.swen.headbanger.BuildConfig;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.activity.MainActivity;

import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.BUTTON_BUILD;
import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.BUTTON_SENSITIVITY;
import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.BUTTON_SIGN_OUT;
import static mddn.swen.headbanger.utilities.UserSettingsController.HeadbangerPreference.EDITTEXT_DISPLAY_NAME;

public class UserSettingsController {

    /* Context to build under */
    private MainActivity mainActivity;

    private Preference sensitivityPreference;

    /**
     * Used to enumerate the type of Preference to be displayed
     */
    public static enum HeadbangerPreference {

        /* List the preferences */
        EDITTEXT_DISPLAY_NAME(R.string.settings_pref_display_name, "display_name"),
        BUTTON_SENSITIVITY(R.string.settings_pref_sensitivity, null),
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
        sensitivityPreference = addButton(generalCategory, BUTTON_SENSITIVITY, "" + getCurrentSensitivity());
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
    private Preference addButton(PreferenceGroup parent, final HeadbangerPreference pref, String value) {
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
        return preference;
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
            case BUTTON_SENSITIVITY:
                showSensitivitySlider();
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
     * Called when the user wishes to adjust the sensitivity. Note has no <i>actual</i> effect.
     */
    private void showSensitivitySlider() {

        /* Build the dialog */
        final AlertDialog.Builder alert = new AlertDialog.Builder(mainActivity);
        alert.setTitle(mainActivity.getString(R.string.settings_sensitivity_title));
        alert.setMessage(mainActivity.getString(R.string.settings_sensitivity_message));
        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.view_dialog_slider, (ViewGroup) mainActivity.findViewById(R.id.seeker_dialog_root));
        alert.setView(layout);

        /* Get the elements */
        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seeker_dialog_bar);
        seekBar.setProgress(getCurrentSensitivity());
        seekBar.setMax(100);
        final TextView value = (TextView) layout.findViewById(R.id.seeker_dialog_text_view);
        value.setText("" + seekBar.getProgress());

        /* Update the text view with the progress */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /* Listen to confirmation */
        alert.setPositiveButton(mainActivity.getString(R.string.settings_sensitivity_confirm),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setNewSensitivity(seekBar.getProgress());
                sensitivityPreference.setSummary(value.getText());
            }
        });

        /* Ignore cancelling */
        alert.setNegativeButton(mainActivity.getString(R.string.settings_sensitivity_cancel), null);

        /* Display */
        alert.show();
    }

    /**
     * Set a new sensitivity preference
     *
     * @param progress
     */
    private void setNewSensitivity(int progress) {

        /* Get the shared prefs */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);

        /* Commit the new value, whatever it is */
        sp.edit().putInt("current_sensitivity", progress).commit();
    }

    /**
     * Get the current sensitivity preference
     *
     * @return
     */
    private int getCurrentSensitivity() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        return sp.getInt("current_sensitivity", 0);
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
