package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mddn.swen.headbanger.R;
import mddn.swen.headbanger.adapter.SongRatingAdapter;

/**
 * Fragment responsible for displaying current song ratings
 */
public class SongRatingFragment extends Fragment {

    @InjectView(R.id.ratings_list_view)
    ListView songRatingList;

    /**
     * Adapter responsible for the list
     */
    private SongRatingAdapter songRatingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings_list, container, false);
        ButterKnife.inject(this, view);
        buildListAdapter();
        return view;
    }

    /**
     * Build the adapter responsible for displaying content on the list view
     */
    private void buildListAdapter() {
        songRatingAdapter = new SongRatingAdapter(getActivity());
        songRatingList.setAdapter(songRatingAdapter);
        songRatingList.setOnItemClickListener(songRatingAdapter);
        songRatingAdapter.setRows(getMeSomeFakeData());
    }

    /**
     * TODO if we can load this from the server that is preferable
     *
     * @return A list of fake song item data
     */
    private List<SongRatingAdapter.SongListItem> getMeSomeFakeData() {
        Map<String, Bitmap> fakeMap = new HashMap<String, Bitmap>();
        fakeMap.put("Shake It Off", BitmapFactory.decodeResource(getResources(), R.drawable.swift));
        fakeMap.put("All About That Bass", BitmapFactory.decodeResource(getResources(), R.drawable.trainor));
        fakeMap.put("Only Love Can Hurt", BitmapFactory.decodeResource(getResources(), R.drawable.faith));
        fakeMap.put("Thinking Out Loud", BitmapFactory.decodeResource(getResources(), R.drawable.sheeran));
        fakeMap.put("I'm Not the Only One", BitmapFactory.decodeResource(getResources(), R.drawable.smith));
        fakeMap.put("Bang Bang", BitmapFactory.decodeResource(getResources(), R.drawable.jessie));
        fakeMap.put("Ugly Heart", BitmapFactory.decodeResource(getResources(), R.drawable.grl));
        fakeMap.put("Budapest", BitmapFactory.decodeResource(getResources(), R.drawable.ezra));
        fakeMap.put("Blame", BitmapFactory.decodeResource(getResources(), R.drawable.harris));
        fakeMap.put("Superheroes", BitmapFactory.decodeResource(getResources(), R.drawable.script));
        List<SongRatingAdapter.SongListItem> items = new ArrayList<SongRatingAdapter.SongListItem>();
        for (String songTitle : fakeMap.keySet()) {
            SongRatingAdapter.SongListItem item = new SongRatingAdapter.SongListItem();
            item.songTitle = songTitle;
            item.albumArt = fakeMap.get(songTitle);
            items.add(item);
        }
        return items;
    }
}
