package mddn.swen.headbanger.activity;

import android.app.Activity;
import android.os.Bundle;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.utilities.User;

/**
 * Special activity designed to handle user preferences.
 *
 * Created by John Quinlivan on 10/03/14.
 * Copyright (c) 2014 Pricemaker. All rights reserved.
 */
public class UserSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        setTitle(R.string.user_settings_title);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User.resume();
    }
}
