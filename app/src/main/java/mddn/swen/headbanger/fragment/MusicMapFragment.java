package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mddn.swen.headbanger.R;
import mddn.swen.headbanger.utilities.GPSTracker;
import mddn.swen.headbanger.utilities.User;

/**
 * Fine grained control over the map fragment
 *
 * Created by John on 18/10/2014.
 */
public class MusicMapFragment extends Fragment {

    /* The interactive map - null if service unavailable */
    private GoogleMap map;
    private MapFragment smf;
    private MarkerOptions marker;

    /* A reference of the GPS tracking service */
    GPSTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_map, container, false);

        smf = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map));
        smf.onCreate(savedInstanceState);
        smf.onResume();

        map = smf.getMap();

        gpsTracker = new GPSTracker(this.getActivity());
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            Toast.makeText(this.getActivity(), "Your location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gpsTracker.showSettingsAlert();
        }
        setUpMap();
        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        smf.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        smf.onResume();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        smf.onDestroy();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        smf.onLowMemory();
    }

    /**
     * Begin processing the map
     */
    private void setUpMap() {
        final LatLng cLatLng;

        if (gpsTracker.canGetLocation()) {
            cLatLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cLatLng, 15);
            map.animateCamera(cameraUpdate);
            marker = new MarkerOptions().position(cLatLng);
        /* Check if the user is available */
            if (User.isLoggedIn()) {
            /* Assign their actual name */
                marker.title(User.getGraphUser().getName());
            /* Attempt to load in their profile picture */
                marker.icon(BitmapDescriptorFactory.fromBitmap(User.profilePicture));
//                User.getProfilePicture(new User.ProfilePicListener() {
//                    @Override
//                    public void onPicLoaded(Bitmap profilePic) {
//                        marker.icon(BitmapDescriptorFactory.fromBitmap(profilePic));
//                    }
//                });
            }
            map.addMarker(marker);
        }
    }

    /**
     * Helper method to return a simple empty map marker with a default title:
     * {@link mddn.swen.headbanger.R.string#map_marker_placeholder_title}
     *
     * And a default image:
     * {@link mddn.swen.headbanger.R.drawable#nowplayingimage}
     *
     * @return An empty default map marker.
     */
    private MarkerOptions emptyMarker() {
        return new MarkerOptions()
                .title(getString(R.string.map_marker_placeholder_title))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.nowplayingimage))
                .anchor(0.5f, 0.5f);
    }

    /**
     * Method for dropping markers using server data about users nearby.
     * @param user
     * @return

    private MarkerOptions userMarkers(User user){
        return new MarkerOptions()
                .title(user.getGraphUser().getName())
                .icon(BitmapDescriptorFactory.fromBitmap(user.profilePicture))
                .anchor(0.5f, 0.5f)
                .position(user.position);
    }
    */
}
