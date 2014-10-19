package mddn.swen.headbanger.navigation;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.fragment.ConnectedDeviceFragment;
import mddn.swen.headbanger.fragment.LoginFragment;
import mddn.swen.headbanger.fragment.MusicMapFragment;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements AdapterView.OnItemClickListener {

    /**
     * Current position in the list
     */
    private static int currentSelectedPosition = -1;

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private boolean userLearnedDrawer;

    /**
     * A preferences key used to store the currently selected index
     */
    private static final String NAV_POSITION = "nav_position";

    @InjectView(R.id.navigation_list_view)
    ListView drawerListView;

    /**
     * The list of navigation items to be shown to the user
     */
    private List<NavigationDrawerItem> items;
    private NavigationDrawerAdapter drawerAdapter;

    /**
     * Android components to tie this into the app
     */
    private DrawerLayout drawerLayout;
    private View fragmentContainerView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get the shared prefs manager */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /* Determine if the user has learned about the nav drawer */
        userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        /* Load the last known selected position - if one exists and is necessary */
        if (currentSelectedPosition == -1) {
            currentSelectedPosition = sp.getInt(NAV_POSITION,
                    NavigationEndpoint.getItemPosition(NavigationEndpoint.DASHBOARD));
        }

        /* Setup the panel */
        setupItems();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer,
                container, false);
        ButterKnife.inject(this, view);
        drawerListView.setOnItemClickListener(this);
        drawerAdapter = new NavigationDrawerAdapter(getActionBar().getThemedContext(), items);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setItemChecked(currentSelectedPosition, true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * Programmatically check if the drawer is presently open
     *
     * @return True if the drawer is visible
     */
    public boolean isDrawerOpen() {
        return drawerLayout != null
                && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Setup the navigation items in the panel
     */
    private void setupItems() {

        /* Instantiate the list */
        items = new ArrayList<NavigationDrawerItem>();

        /* Must have a valid context */
        Context context = getActivity();
        if (context == null) {
            return;
        }

        /* Iterate for all the possible endpoints */
        for (NavigationEndpoint endpoint : NavigationEndpoint.values()) {
            NavigationDrawerItem item;
            switch (endpoint) {
                case DASHBOARD:
                    item = new NavigationDrawerItem(
                            "DASHBOARD",
                            NavigationDrawerItem.NavigationDrawerItemType.NAVIGATION_ROW,
                            ConnectedDeviceFragment.class);
                    break;
                case MAP:
                    item = new NavigationDrawerItem(
                            "MAP",
                            NavigationDrawerItem.NavigationDrawerItemType.NAVIGATION_ROW,
                            MusicMapFragment.class);
                    break;
                case ACCOUNT:
                    item = new NavigationDrawerItem(
                            "MY ACCOUNT",
                            NavigationDrawerItem.NavigationDrawerItemType.NAVIGATION_ROW,
                            LoginFragment.class);
                    break;
                default:
                    item = null;
            }
            items.add(item);
        }
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions for
     * the Activity that it is executing under
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {

        /* Reference the layout */
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;

        /* set a custom shadow that overlays the main content when the drawer opens */
        drawerLayout.setDrawerShadow(R.drawable.drawer_left_shadow, GravityCompat.START);

        /* set up the drawer's list view with items and click listener */
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        /* ActionBarDrawerToggle ties together the the proper interactions
         * between the navigation drawer and the action bar app icon. */
        drawerToggle = new HeadbangerActionBarToggle(
                getActivity(),                    /* host Activity */
                drawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                rememberDrawerOpen();
                getActivity().invalidateOptionsMenu();
            }
        };

        /* Enable the indicator */
        drawerToggle.setDrawerIndicatorEnabled(true);

        /* Defer code dependent on restoration of previous instance state. */
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(drawerToggle);
    }

    /**
     * Called when the Activity has completed other creation operations, will check to see
     * if this should be made visible in the event that the user has not learned about the nav
     * drawer yet.
     */
    public void checkIfUserLearnedDrawer() {
        if (!userLearnedDrawer) {
            rememberDrawerOpen();
            drawerLayout.openDrawer(fragmentContainerView);
        }
    }

    /**
     * The drawer has been opened, remember that the user has now seen the drawer and will not want
     * to have it automatically opened again
     */
    private void rememberDrawerOpen() {
        if (!userLearnedDrawer) {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
        }
        userLearnedDrawer = true;
    }

    /**
     * Select the item in the list at the given position
     *
     * @param position Position in the list to select
     */
    public void selectItem(int position) {

        /* Sanity check */
        if (position < 0 || position >= items.size()) {
            return;
        }
        if (drawerListView != null) {
            drawerListView.setItemChecked(position, true);
        }
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }

        /* Make sure we don't recreate the same fragment */
        Class clazz = getItemAtIndex(position).item;
        if (Fragment.class.isAssignableFrom(clazz)) {
            Fragment current = getFragmentManager().findFragmentById(R.id.container);
            if (current != null && current.getClass().equals(clazz)) {
                return;
            }
        }

        /* Select the position */
        onNavigationDrawerItemSelected(position);
    }

    /**
     * Returns the current action bar in the app
     *
     * @return The action bar
     */
    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Get the navigation drawer item located at the passed index
     *
     * @param position the index of the item to get
     * @return the item at the index passed
     */
    public NavigationDrawerItem getItemAtIndex(int position) {
        if (position >= items.size()) {
            position = items.size() - 1;
        }
        return items.get(position);
    }

    /**
     * Handles the action of the user selecting an item at the given index
     *
     * @param position Position in the navigation list to toggle to
     */
    public void onNavigationDrawerItemSelected(int position) {

        /* Get the navigation item */
        NavigationDrawerItem itemSelected = getItemAtIndex(position);

        /* Is this a fragment to switch the current fragment out with? */
        if (Fragment.class.isAssignableFrom(itemSelected.item)) {
            try {

                /* Create new fragment through reflection */
                Constructor constructor = itemSelected.item.getConstructor();
                Fragment fragment = (Fragment) constructor.newInstance();

                /* Perform a new transaction to replace the fragment */
                FragmentManager fragmentManager = getFragmentManager();

                /* Transition to the new fragment */
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.container, fragment)
                        .commit();

                /*
                 * This is the only time the current position is actually updated. Any other
                 * endpoint invokes a navigation away from here, when the user returns, it should
                 * be to a valid fragment
                 */
                currentSelectedPosition = position;
                writeCurrentIndex();

                /* Bold this */
                for (NavigationDrawerItem item : items) {
                    item.setAttached(false);
                    if (item == itemSelected) {
                        itemSelected.setAttached(true);
                    }
                }

            } catch (Exception e) {
                Log.e("" + NavigationDrawerFragment.class, e.toString());
            }
        }
    }

    /**
     * Will write the currently selected index to shared prefs. When this is created again, this
     * index will be referenced.
     */
    private void writeCurrentIndex() {
        PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .edit()
                .putInt(NAV_POSITION, currentSelectedPosition)
                .commit();
    }

    /**
     * Will return the currently selected position in the navigation drawer.
     *
     * @return The currently selected position on the navigation drawer
     */
    public int getCurrentSelectedPosition() {
        return currentSelectedPosition;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= items.size()
                || items.get(position).type == NavigationDrawerItem.NavigationDrawerItemType.NAVIGATION_DIVIDER) {
            return;
        }
        selectItem(position);
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    /**
     * References of fragments that exist on the Navigation Drawer
     */
    public enum NavigationEndpoint {
        DASHBOARD,
        MAP,
        ACCOUNT;

        public static int getItemPosition(NavigationEndpoint endpoint) {
            return Arrays.asList(NavigationEndpoint.values()).indexOf(endpoint);
        }

        public static NavigationEndpoint getEndpoint(int itemPosition) {
            if (itemPosition < 0 || itemPosition > NavigationEndpoint.values().length) {
                return DASHBOARD;
            } else {
                return NavigationEndpoint.values()[itemPosition];
            }
        }
    }
}