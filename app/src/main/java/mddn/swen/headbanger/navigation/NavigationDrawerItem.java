package mddn.swen.headbanger.navigation;

/**
 * A navigation drawer item is a data structure to be used to present
 * the user with different navigation design list items.
 */
public class NavigationDrawerItem {

    /* Fields of this item */
    public final String title;
    public final NavigationDrawerItemType type;
    public final Class<?> item;
    private boolean isAttached;

    /**
     * Constructor for creating a navigation drawer item.
     *
     * @param title     the title of the item
     * @param type      the type of view to draw this item as
     * @param item      The raw item this references, used to quickly and elegantly open the item
     */
    public NavigationDrawerItem(String title, NavigationDrawerItemType type, Class<?> item) {
        this.title = title;
        this.type = type;
        this.item = item;
    }

    /**
     * Is this item currently attached to an activity
     *
     * @return true if attached to an activity
     */
    public boolean isAttached() {
        return isAttached;
    }

    /**
     * Set whether or not this view is attached to an activity
     *
     * @param attached true if attached to an activity
     */
    public void setAttached(boolean attached) {
        isAttached = attached;
    }

    /**
     * The types of navigation items that will sit in a navigation drawer.
     */
    public enum NavigationDrawerItemType {

        /**
         * A navigation item that is logical to press
         */
        NAVIGATION_ROW,

        /**
         * Used specifically for the divider cell
         */
        NAVIGATION_DIVIDER;
    }

}
