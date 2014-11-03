package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.utilities.GPSTracker;
import mddn.swen.headbanger.utilities.MapUser;
import mddn.swen.headbanger.utilities.User;

/**
 * Fine grained control over the map fragment
 *
 * Created by John on 18/10/2014.
 */
public class MusicMapFragment extends Fragment {

    /* The interactive map - null if service unavailable */
    private GoogleMap map;
    private MapFragment mapFragment;

    /* A reference of the GPS tracking service */
    GPSTracker gpsTracker;

    /* Reference the map markers */
    public HashMap<String, MapUser> markers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Inflate the layout */
        View view = inflater.inflate(R.layout.fragment_music_map, container, false);

        /* Reference elements */
        mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map));
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();
        map = mapFragment.getMap();

        /* Check local GPS */
        gpsTracker = new GPSTracker(getActivity());
        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
        }

        /* Establish the map markers */
        markers = new HashMap<String, MapUser>();
        fakeData();

        /* Assign the window adapter */
        addWindowAdapter();

        /* Return the populated view */
        return view;
    }

    /**
     * Handles the window adapter
     */
    private void addWindowAdapter() {
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                 /* Inflate the containing view */
                View markerView = mapFragment
                        .getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.view_map_marker, null);

                /* Inflate views */
                ImageView userPic = (ImageView) markerView.findViewById(R.id.marker_user_profile);
                TextView userName = (TextView) markerView.findViewById(R.id.marker_user_name);
                TextView lastSongTitle = (TextView) markerView.findViewById(R.id.marker_user_last_song_title);
                TextView lastSongRating = (TextView) markerView.findViewById(R.id.marker_user_last_song_rating);

                /* Populate values */
                MapUser mapUser = markers.get(marker.getId());
                userPic.setImageBitmap(mapUser.userPic);
                userName.setText(mapUser.name);
                lastSongTitle.setText(mapUser.lastSong);
                lastSongRating.setText(mapUser.lastSongRating);

                /* Return the view */
                return markerView;
            }
        });
    }

    /**
     * Populates the marker list with fake data
     */
    private void fakeData() {
        addCurrentUser();
        for (int i = 0; i < 5; i++) {
            MapUser mapUser = new MapUser();
            LatLng coordinates = null;
            if (i == 0) {
                mapUser.userPic = BitmapFactory.decodeResource(getResources(), R.drawable.john);
                mapUser.lastSong = "Shake it off";
                mapUser.name = "Taylor Swift";
                mapUser.lastSongRating = "" + ((int) (Math.random() * 5.0));
                coordinates = new LatLng(-41.296, 174.778);
            } else if (i == 1) {
                mapUser.userPic = BitmapFactory.decodeResource(getResources(), R.drawable.pragya);
                mapUser.lastSong = "Budapest";
                mapUser.name = "George Ezra";
                mapUser.lastSongRating = "" + ((int) (Math.random() * 5.0));
                coordinates = new LatLng(-41.297, 174.775);
            } else if (i == 2) {
                mapUser.userPic = BitmapFactory.decodeResource(getResources(), R.drawable.patrick);
                mapUser.lastSong = "All About That Bass";
                mapUser.name = "Meghan Trainor";
                mapUser.lastSongRating = "" + ((int) (Math.random() * 5.0));
                coordinates = new LatLng(-41.298, 174.782);
            } else if (i == 3) {
                mapUser.userPic = BitmapFactory.decodeResource(getResources(), R.drawable.jobs);
                mapUser.lastSong = "Only Love Can Hurt";
                mapUser.name = "Paloma Faith";
                mapUser.lastSongRating = "" + ((int) (Math.random() * 5.0));
                coordinates = new LatLng(-41.294, 174.77);
            } else {
                mapUser.userPic = BitmapFactory.decodeResource(getResources(), R.drawable.ballmer);
                mapUser.lastSong = "Thinking Out Loud";
                mapUser.name = "Ed Sheeran";
                mapUser.lastSongRating = "" + ((int) (Math.random() * 5.0));
                coordinates = new LatLng(-41.293, 174.776);
            }
            if (coordinates != null) {
                Marker marker = map.addMarker(new MarkerOptions().position(coordinates));
                markers.put(marker.getId(), mapUser);
            }
        }
    }

    /**
     * Add the current user to the map marker data
     */
    private void addCurrentUser() {

        /* Check status */
        if (gpsTracker.canGetLocation() && User.isLoggedIn()) {

            /* Navigate map */
            final LatLng cLatLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cLatLng, 15);
            map.animateCamera(cameraUpdate);

            /* Build their marker */
            Marker marker = map.addMarker(new MarkerOptions().position(cLatLng));

            /* Create user data */
            final MapUser mapUser = new MapUser();
            mapUser.userPic = BitmapFactory.decodeResource(getResources(), R.drawable.swift);
            mapUser.lastSong = "Shake it off";
            mapUser.name = "Taylor Swift";
            mapUser.lastSongRating = "5";

            /* Add to major map */
            markers.put(marker.getId(), mapUser);

            /* Attempt to load in their profile picture */
            User.getProfilePicture(new User.ProfilePicListener() {

                @Override
                public void onPicLoaded(Bitmap profilePic) {
                    mapUser.userPic = profilePic;
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "Error loading GPS coordinates, Are you signed in?", Toast.LENGTH_LONG);
            Log.e(MusicMapFragment.class.toString(), "USER DETAILS ERROR. Is GPS working and are you logged in?");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mapFragment.onResume();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (map != null) {
            getActivity()
                    .getFragmentManager()
                    .beginTransaction()
                    .remove(getActivity().getFragmentManager().findFragmentById(R.id.map))
                    .commit();
        }
        map = null;
        mapFragment = null;
        gpsTracker = null;
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapFragment.onLowMemory();
    }
}
