package my.app.chordmate;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;
import com.google.android.material.button.MaterialButton;

public class FullscreenChordActivity extends AppCompatActivity {
    private AudioPlayer audioPlayer;
    private String currentChordName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_chord);

        ImageView chordImage = findViewById(R.id.fullscreen_chord_image);
        TextView chordNameTextView = findViewById(R.id.chord_name);
        TextView chordDescriptionTextView = findViewById(R.id.chord_description);
        ImageButton backButton = findViewById(R.id.back_button);
        MaterialButton playAudioButton = findViewById(R.id.play_chord_audio_btn);

        int imageResId = getIntent().getIntExtra("chord_image", R.drawable.default_image);
        currentChordName = getIntent().getStringExtra("chord_name");
        String chordDescription = getIntent().getStringExtra("chord_description");

        if (currentChordName == null) {
            currentChordName = "";
        }

        if (chordDescription == null) {
            chordDescription = "No description available for this chord.";
        }

        Log.d("FullscreenChordActivity", "Chord Name: " + currentChordName);

        chordImage.setImageResource(imageResId);
        chordNameTextView.setText(currentChordName);
        chordDescriptionTextView.setText(chordDescription);

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

            case "bb":
            case "b flat major (bb)":
            case "b flat major":
            case "b flat":
                return R.raw.bb_audio;

            case "bm":
            case "b minor (bm)":
            case "b minor":
                return R.raw.bm_audio;

            case "c":
            case "c major (c)":
            case "c major":
                return R.raw.c_audio;

            case "d major (d)":
            case "d major":
            case "d":
                return R.raw.d_audio;

            case "d7":
            case "d dominant 7th (d7)":
            case "d dominant 7th":
            case "d seventh":
                return R.raw.d7_audio;

            case "dm":
            case "d minor":
            case "d minor (dm)":
                return R.raw.dm_audio;

            case "e major (e)":
            case "e major":
            case "e":
                return R.raw.e_audio;

            case "e minor (em)":
            case "em":
            case "e minor":
                return R.raw.em_audio;

            case "f":
            case "f major (f)":
            case "f major":
                return R.raw.f_audio;

            case "g major (g)":
            case "g major":
            case "g":
                return R.raw.g_audio;

            case "a":
            case "a major":
            case "a major (a)":
                return R.raw.a_audio;

            case "b7":
            case "b7 major":
            case "b7 major (b7)":
                return R.raw.b7_audio;

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