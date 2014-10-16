package mddn.swen.headbanger.navigation;

import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Created by Alex Quinlivan on 9/04/14.
 * Copyright (c) 2014 Pricemaker. All rights reserved.
 */
public class PricemakerActionBarToggle extends ActionBarDrawerToggle {

    private final DrawerLayout drawerLayout;

    public PricemakerActionBarToggle(Activity activity, DrawerLayout drawerLayout,
                                 int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.drawerLayout = drawerLayout;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            if (!drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            return true;
        }
        return false;
    }

}