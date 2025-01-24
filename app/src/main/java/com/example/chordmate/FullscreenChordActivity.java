package com.example.chordmate;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FullscreenChordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_chord);

        ImageView chordImage = findViewById(R.id.fullscreen_chord_image);
        ImageButton backButton = findViewById(R.id.back_button);

        int imageResId = getIntent().getIntExtra("chord_image", R.drawable.default_image);
        chordImage.setImageResource(imageResId);

        backButton.setOnClickListener(v -> finish());
    }
}
