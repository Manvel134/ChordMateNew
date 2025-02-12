package my.app.chordmate;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;

public class FullscreenChordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_chord);

        ImageView chordImage = findViewById(R.id.fullscreen_chord_image);
        TextView chordName = findViewById(R.id.chord_name);
        ImageButton backButton = findViewById(R.id.back_button);

        int imageResId = getIntent().getIntExtra("chord_image", R.drawable.default_image);
        String name = getIntent().getStringExtra("chord_name");

        chordImage.setImageResource(imageResId);
        chordName.setText(name);

        backButton.setOnClickListener(v -> finish());
    }
}
