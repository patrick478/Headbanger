package mddn.swen.headbanger.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;

/**
 * Adapter responsible for the Song Rating list
 *
 * Created by John on 22/10/2014.
 */
public class SongRatingAdapter extends ArrayAdapter<SongRatingAdapter.SongListItem>
        implements AdapterView.OnItemClickListener {

    /**
     * Stores a list of rows
     */
    private List<SongListItem> rowList;

    /**
     * Preferred constructor
     *
     * @param context Context executing under
     */
    public SongRatingAdapter(Context context) {
        super(context, R.layout.list_ratings_row);
        rowList = new ArrayList<SongListItem>();
    }

    /**
     * Sets the rows to this adapter, will invoke a refresh of the content
     *
     * @param newList A list of song ratings to use
     */
    public void setRows(List<SongListItem> newList) {
        rowList = newList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_ratings_row, parent, false);
        }
        ViewHolder holder;
        if (convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        }
        else {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.listPosition.setText((position + 1) + ".");
        holder.albumArt.setImageBitmap(rowList.get(position).albumArt);
        holder.songTitle.setText(rowList.get(position).songTitle);
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String songTitle = rowList.get(position).songTitle;
        Uri uri = Uri.parse("http://www.youtube.com/results?search_query=" + songTitle);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

    @Override
    public int getCount() {
        return rowList.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * View holder for the row data
     */
    public static class ViewHolder {

        @InjectView(R.id.list_position)
        TextView listPosition;

        @InjectView(R.id.song_title)
        TextView songTitle;

        @InjectView(R.id.album_art)
        ImageView albumArt;

        public ViewHolder(View rowView) {
            ButterKnife.inject(this, rowView);
        }
    }

    /**
     * A simple class representing a list item
     */
    public static class SongListItem {
        public String songTitle;
        public Bitmap albumArt;
    }
}
