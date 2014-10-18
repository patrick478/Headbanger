package mddn.swen.headbanger.navigation;

import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

public class HeadbangerActionBarToggle extends ActionBarDrawerToggle {

    private final DrawerLayout drawerLayout;

    public HeadbangerActionBarToggle(Activity activity, DrawerLayout drawerLayout,
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