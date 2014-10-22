package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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
        List<Object> newList = new ArrayList<Object>(); //TODO obviously silly
        newList.add(new Object());
        songRatingAdapter.setRows(newList);
    }


}
