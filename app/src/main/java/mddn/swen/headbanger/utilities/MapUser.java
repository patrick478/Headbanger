package mddn.swen.headbanger.utilities;

import android.graphics.Bitmap;

/**
 * An instance of a user that is used to display information on a map
 *
 * Created by John Quinlivan on 2/11/14.
 */
public class MapUser {
    public Bitmap userPic;
    public String name;
    public String lastSong;
    public String lastSongRating;

    @Override
    public String toString() {
        return "MapUser" +
                ". User pic: " + userPic +
                ". Name: " + name +
                ". Last Song: " + lastSong +
                ". Last Song Rating: " + lastSongRating;
    }
}
