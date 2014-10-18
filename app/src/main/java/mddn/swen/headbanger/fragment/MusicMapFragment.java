package mddn.swen.headbanger.fragment;

import android.app.Fragment;
import android.os.Bundle;
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

/**
 * Fine grained control over the map fragment
 *
 * Created by John on 18/10/2014.
 */
public class MusicMapFragment extends Fragment {

    /* The interactive map - null if service unavailable */
    private GoogleMap map;

    /* A reference of the GPS tracking service */
    GPSTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_map, container, false);
        MapFragment smf = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map));

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

    /**
     * Begin processing the map
     */
    private void setUpMap() {
        LatLng cLatLng;
        if(gpsTracker.canGetLocation()) {
            cLatLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cLatLng, 15);
            map.animateCamera(cameraUpdate);

            /* TODO use the current facebook user's profile pic */
            map.addMarker(new MarkerOptions().position(cLatLng).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.nowplayingimage)).anchor(0.5f, 0.5f));
        }
    }
}
