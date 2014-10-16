package mddn.swen.headbanger.navigation;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;

/**
 * Adapter used for the Navigation Drawer
 */
public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {

    /* Item identifiers */
    private int ITEM_ROW     = 0;
    private int ITEM_DIVIDER = 1;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> source) {
        super(context, R.layout.list_navigation_row, source);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        /* Reference the view holder */
        ViewHolder holder;

        /* Get the item at this index */
        NavigationDrawerItem item = getItem(position);

        /* Exit early if a divider is detected */
        if (item.type == NavigationDrawerItem.NavigationDrawerItemType.NAVIGATION_DIVIDER) {
            if (view == null){
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_navigation_divider, parent, false);
            }
            return view;
        }

        /* Pull out the view holder */
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        }

        /* Need to generate a new one */
        else {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_navigation_row, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        /* Assign the row text */
        holder.title.setText(item.title);

        /* Toggle bold if this is the currently selected index */
        if (item.type == NavigationDrawerItem.NavigationDrawerItemType.NAVIGATION_ROW) {
            if (item.isAttached()) {
                holder.title.setTypeface(null, Typeface.BOLD);
            } else {
                holder.title.setTypeface(null, Typeface.NORMAL);
            }
        }

        /* Return the view */
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        switch (getItem(position).type) {
            case NAVIGATION_ROW:
                return ITEM_ROW;
            case NAVIGATION_DIVIDER:
            default:
                return ITEM_DIVIDER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2; //Actual items and dividers
    }

    /**
     * View holder class
     */
    public static class ViewHolder {

        @InjectView(R.id.navigation_title)
        TextView title;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
