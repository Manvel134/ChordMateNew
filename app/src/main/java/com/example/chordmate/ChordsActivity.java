package com.example.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chords);

        Button backToMenuButton = findViewById(R.id.back_to_menu_button);

        backToMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChordsActivity.this, MainMenuActivity.class);
            startActivity(intent);

        });
        setupChordCardClick(findViewById(R.id.am_card), R.drawable.am_chord);
        setupChordCardClick(findViewById(R.id.em_card), R.drawable.em_chord);

    }

    private void setupChordCardClick(View cardView, int imageResId) {
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, FullscreenChordActivity.class);
            intent.putExtra("chord_image", imageResId);
            startActivity(intent);
        });
    }

}
