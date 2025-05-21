package my.app.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;
import com.google.android.material.button.MaterialButton;

public class MyChordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chords);

        // Initialize UI elements
        View emptyStateContainer = findViewById(R.id.empty_state_container);
        View chordsGrid = findViewById(R.id.chords_grid);
        MaterialButton addChordButton = findViewById(R.id.add_chord_button);
        MaterialButton backButton = findViewById(R.id.back_to_menu_button);

        // For now, we always show the empty state
        emptyStateContainer.setVisibility(View.VISIBLE);
        chordsGrid.setVisibility(View.GONE);

        // Set click listeners
        addChordButton.setOnClickListener(v -> {
            // For now, just show a toast message
            // In a real implementation, you would navigate to a chord selection screen
            Toast.makeText(MyChordsActivity.this, "Add chord feature coming soon!", Toast.LENGTH_SHORT).show();

            // Optionally navigate to the chords library
            // Intent intent = new Intent(MyChordsActivity.this, ChordsActivity.class);
            // startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());
    }
}