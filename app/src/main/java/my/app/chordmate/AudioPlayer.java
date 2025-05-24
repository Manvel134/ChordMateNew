package my.app.chordmate;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private Context context;

    public AudioPlayer(Context context) {
        this.context = context;
    }

    public void playAudio(int resourceId) {
        release();
        try {
            mediaPlayer = MediaPlayer.create(context, resourceId);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> release());
            }
        } catch (Exception e) {
            Log.e("AudioPlayer", "Error playing audio resource: " + e.getMessage());
        }
    }

    public void playAudio(Uri audioUri) {
        release();
        try {
            mediaPlayer = MediaPlayer.create(context, audioUri);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> release());
            }
        } catch (Exception e) {
            Log.e("AudioPlayer", "Error playing audio URI: " + e.getMessage());
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}