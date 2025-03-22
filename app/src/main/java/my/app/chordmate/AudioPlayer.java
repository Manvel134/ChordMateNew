package my.app.chordmate;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer {

    private MediaPlayer mediaPlayer;
    private Integer currentAudioResId;
    private Context context;

    public AudioPlayer(Context context) {
        this.context = context;
        this.mediaPlayer = null;
        this.currentAudioResId = null;
    }

    public void playAudio(int audioResId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, audioResId);
            currentAudioResId = audioResId;
            mediaPlayer.setOnCompletionListener(mp -> stopAudio());
            mediaPlayer.start();
        } else {
            if (currentAudioResId == audioResId) {
                if (mediaPlayer.isPlaying()) {
                    stopAudio();
                } else {
                    mediaPlayer.start();
                }
            } else {
                stopAudio();
                mediaPlayer = MediaPlayer.create(context, audioResId);
                currentAudioResId = audioResId;
                mediaPlayer.setOnCompletionListener(mp -> stopAudio());
                mediaPlayer.start();
            }
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}