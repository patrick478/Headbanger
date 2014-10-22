package mddn.swen.headbanger.adapter;

import android.content.Context;
import android.content.Intent;
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
public class SongRatingAdapter extends ArrayAdapter<Object> implements AdapterView.OnItemClickListener {

    /**
     * Stores a list of rows
     */
    private List<Object> rowList; //TODO shouldn't be Object

    /**
     * Preferred constructor
     *
     * @param context Context executing under
     */
    public SongRatingAdapter(Context context) {
        super(context, R.layout.list_ratings_row);
        rowList = new ArrayList<Object>();
    }

    /**
     * Sets the rows to this adapter, will invoke a refresh of the content
     *
     * @param newList A list of song ratings to use
     */
    public void setRows(List<Object> newList) {
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
//        holder.albumArt.setImageBitmap(); TODO
        holder.songTitle.setText("A Song Title"); //TODO
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String songTitle = "A song title"; //TODO fetch from list
        Uri uri = Uri.parse("http://www.google.com/#q=" + songTitle);
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

        @InjectView(R.id.song_title)
        TextView songTitle;

        @InjectView(R.id.album_art)
        ImageView albumArt;

        public ViewHolder(View rowView) {
            ButterKnife.inject(this, rowView);
        }
    }
}
