package mddn.swen.headbanger.utilities;

/**
 * Created by Pragya on 22/10/14.
 */
public class MusicPlayer{

    private boolean playing;
    private boolean nextLoaded;
    private boolean previousLoaded;

    //TODO: get notifications of audio track changes so that the nod count can be reset for each track


    public void skipToNext() {

        //TODO: skip to the next song!
    }

    public void skipToPrevious() {

        //TODO: skip to the previous song!
    }

    public void pauseMusic() {

        //TODO: pause the music!
    }

    public void playMusic() {

        //TODO: play the music!
    }

    public boolean isPaused() {
        return !playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean hasPreviousTrackLoaded() {
        return previousLoaded;
    }

    public boolean hasNextTrackLoaded() {
        return nextLoaded;
    }
}
