package my.app.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;
import com.google.android.material.button.MaterialButton;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Initialize UI elements
        MaterialButton practiceButton = findViewById(R.id.practice_button);
        MaterialButton myChordsButton = findViewById(R.id.my_chords_button);
        MaterialButton chordsButton = findViewById(R.id.chords_button);
        MaterialButton profileButton = findViewById(R.id.profile_button);
        ImageView profileIcon = findViewById(R.id.profile_icon);

        // Set click listeners
        practiceButton.setOnClickListener(v -> {
            // Fixed: Navigate to DifficultySelection instead of MainActivity
            Intent intent = new Intent(MainMenuActivity.this, DifficultySelection.class);
            startActivity(intent);
        });

        myChordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, MyChordsActivity.class);
            startActivity(intent);
        });

        chordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ChordsActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}