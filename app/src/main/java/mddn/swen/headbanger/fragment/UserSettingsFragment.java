package mddn.swen.headbanger.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import mddn.swen.headbanger.activity.MainActivity;
import mddn.swen.headbanger.utilities.UserSettingsController;

/**
 * Small helper fragment to load the preferences into
 * <p/>
 * Created by John Quinlivan on 10/03/14.
 * Copyright (c) 2014 Pricemaker. All rights reserved.
 */
public class UserSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    /* Instance of the settings controller */
    private UserSettingsController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen prefs = getPreferenceManager().createPreferenceScreen(getActivity());
        prefs.setOnPreferenceChangeListener(this);
        controller = new UserSettingsController((MainActivity) getActivity(), prefs);
        setPreferenceScreen(prefs);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(UserSettingsFragment.class.toString(), "New Val: " + newValue);
        return true;
    }
}
