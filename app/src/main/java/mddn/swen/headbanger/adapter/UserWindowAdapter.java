package mddn.swen.headbanger.adapter;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * An adapter that will display user information to the Google Map
 * 
 * Created by John Quinivan on 29/10/14.
 */
public class UserWindowAdapter implements GoogleMap.InfoWindowAdapter {
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
