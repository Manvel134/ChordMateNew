package my.app.chordmate;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;

public class FullscreenChordActivity extends AppCompatActivity {
    private AudioPlayer audioPlayer;
    private String currentChordName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_chord);

        ImageView chordImage = findViewById(R.id.fullscreen_chord_image);
        TextView chordNameTextView = findViewById(R.id.chord_name);
        ImageButton backButton = findViewById(R.id.back_button);
        Button playAudioButton = findViewById(R.id.play_chord_audio_btn);

        int imageResId = getIntent().getIntExtra("chord_image", R.drawable.default_image);
        currentChordName = getIntent().getStringExtra("chord_name");

        if (currentChordName == null) {
            currentChordName = "";
        }

        Log.d("FullscreenChordActivity", "Chord Name: " + currentChordName);

        chordImage.setImageResource(imageResId);
        chordNameTextView.setText(currentChordName);

        audioPlayer = new AudioPlayer(this);

        playAudioButton.setOnClickListener(v -> playCurrentChordAudio());
        backButton.setOnClickListener(v -> finish());
    }

    private void playCurrentChordAudio() {
        int audioResId = getAudioResId(currentChordName);

        if (audioResId == 0) {
            Log.e("FullscreenChordActivity", "Audio resource not found for: " + currentChordName);
            return;
        }

        Log.d("FullscreenChordActivity", "Playing audio for: " + currentChordName);
        audioPlayer.playAudio(audioResId);
    }

    private int getAudioResId(String chordName) {
        chordName = chordName.trim().toLowerCase(); // Normalize input

        switch (chordName) {
            case "am":
            case "a minor (am)":
            case "a minor":
                return R.raw.am_audio;

            case "d major (d)":
            case "d major":
            case "d":
                return R.raw.d_audio;

            case "e major (e)":
            case "e major":
            case "e":
                return R.raw.e_audio;

            case "e minor (em)":
            case "em":
            case "e minor":
                return R.raw.em_audio;

            case "g major (g)":
            case "g major":
            case "g":
                return R.raw.g_audio;

            case "dm":
            case "d minor":
            case "d minor (dm)":
                return R.raw.dm_audio;
            default:
                Log.e("FullscreenChordActivity", "Unknown chord: " + chordName);
                return 0;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioPlayer.release();
    }
}
