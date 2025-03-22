package my.app.chordmate;

import android.os.Bundle;
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
        ImageButton playAudioButton = findViewById(R.id.play_chord_audio_btn);

        int imageResId = getIntent().getIntExtra("chord_image", R.drawable.default_image);
        currentChordName = getIntent().getStringExtra("chord_name");

        chordImage.setImageResource(imageResId);
        chordNameTextView.setText(currentChordName);

        audioPlayer = new AudioPlayer(this);

        playAudioButton.setOnClickListener(v -> playCurrentChordAudio());
        backButton.setOnClickListener(v -> finish());
    }

    private void playCurrentChordAudio() {
        int audioResId = getAudioResId(currentChordName);
        audioPlayer.playAudio(audioResId);
    }

    private int getAudioResId(String chordName) {
        switch (chordName) {
            case "Am":
                return R.raw.am_audio;
            case "D":
                return R.raw.d_audio;
            case "E":
                return R.raw.e_audio;
            case "Em":
                return R.raw.em_audio;
            case "G":
                return R.raw.g_audio;
            case "Dm":
                return R.raw.dm_audio;
            default:
                return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioPlayer.release();
    }
}