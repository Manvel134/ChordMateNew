package my.app.chordmate;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chordmate.R;
import com.google.android.material.button.MaterialButton;
import java.io.File;

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

        int imageResId = getIntent().getIntExtra("chord_image", 0);
        String imagePath = getIntent().getStringExtra("chord_image_path");
        String audioPath = getIntent().getStringExtra("chord_audio_path");
        currentChordName = getIntent().getStringExtra("chord_name");
        String chordDescription = getIntent().getStringExtra("chord_description");

        if (currentChordName == null) {
            currentChordName = "";
        }

        if (chordDescription == null) {
            chordDescription = "No description available for this chord.";
        }

        // Set image
        if (imagePath != null) {
            chordImage.setImageURI(Uri.fromFile(new File(imagePath)));
        } else if (imageResId != 0) {
            chordImage.setImageResource(imageResId);
        } else {
            chordImage.setImageResource(R.drawable.default_image);
        }

        chordNameTextView.setText(currentChordName);
        chordDescriptionTextView.setText(chordDescription);

        audioPlayer = new AudioPlayer(this);

        playAudioButton.setOnClickListener(v -> {
            if (audioPath != null) {
                audioPlayer.playAudio(Uri.fromFile(new File(audioPath)));
            } else {
                playCurrentChordAudio();
            }
        });

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
        chordName = chordName.trim().toLowerCase();
        switch (chordName) {
            case "a":
            case "a major":
            case "a major (a)":
                return R.raw.a_audio;

            case "bb":
            case "b flat major (bb)":
            case "b flat major":
            case "b flat":
                return R.raw.bb_audio;

            case "c":
            case "c major (c)":
            case "c major":
                return R.raw.c_audio;

            case "d major (d)":
            case "d major":
            case "d":
                return R.raw.d_audio;

            case "e major (e)":
            case "e major":
            case "e":
                return R.raw.e_audio;

            case "f":
            case "f major (f)":
            case "f major":
                return R.raw.f_audio;

            case "g major (g)":
            case "g major":
            case "g":
                return R.raw.g_audio;

            // Minor Chords
            case "am":
            case "a minor (am)":
            case "a minor":
                return R.raw.am_audio;

            case "bm":
            case "b minor (bm)":
            case "b minor":
                return R.raw.bm_audio;

            case "c#m":
            case "c# minor (c#m)":
            case "c# minor":
            case "c sharp minor":
                return R.raw.c_sharp_m_audio;

            case "dm":
            case "d minor":
            case "d minor (dm)":
                return R.raw.dm_audio;

            case "e minor (em)":
            case "em":
            case "e minor":
                return R.raw.em_audio;

            case "f#m":
            case "f# minor (f#m)":
            case "f# minor":
            case "f sharp minor":
                return R.raw.f_sharp_m_audio;

            case "g#m":
            case "g# minor (g#m)":
            case "g# minor":
            case "g sharp minor":
                return R.raw.g_sharp_m_audio;

            // Seventh Chords
            case "a7":
            case "a dominant 7th (a7)":
            case "a dominant 7th":
            case "a seventh":
                return R.raw.a7_audio;

            case "am7":
            case "a minor 7th (am7)":
            case "a minor 7th":
            case "a minor seventh":
                return R.raw.am7_audio;

            case "amaj7":
            case "a major 7th (amaj7)":
            case "a major 7th":
            case "a major seventh":
                return R.raw.amaj7_audio;

            case "b7":
            case "b7 major":
            case "b7 major (b7)":
            case "b dominant 7th (b7)":
            case "b dominant 7th":
                return R.raw.b7_audio;

            case "c7":
            case "c dominant 7th (c7)":
            case "c dominant 7th":
            case "c seventh":
                return R.raw.c7_audio;

            case "cmaj7":
            case "c major 7th (cmaj7)":
            case "c major 7th":
            case "c major seventh":
                return R.raw.cmaj7_audio;

            case "d7":
            case "d dominant 7th (d7)":
            case "d dominant 7th":
            case "d seventh":
                return R.raw.d7_audio;

            case "dmaj7":
            case "d major 7th (dmaj7)":
            case "d major 7th":
            case "d major seventh":
                return R.raw.dmaj7_audio;

            case "e7":
            case "e dominant 7th (e7)":
            case "e dominant 7th":
            case "e seventh":
                return R.raw.e7_audio;

            case "g7":
            case "g dominant 7th (g7)":
            case "g dominant 7th":
            case "g seventh":
                return R.raw.g7_audio;

            // Extended and Sus Chords
            case "d6":
            case "d major 6th (d6)":
            case "d major 6th":
            case "d sixth":
                return R.raw.d6_audio;

            case "esus4":
            case "e suspended 4th (esus4)":
            case "e suspended 4th":
            case "e sus 4":
                return R.raw.esus4_audio;

            case "g6":
            case "g major 6th (g6)":
            case "g major 6th":
            case "g sixth":
                return R.raw.g6_audio;

            default:
                Log.e("FullscreenChordActivity", "Unknown chord: " + chordName);
                return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.release();
        }
    }
}