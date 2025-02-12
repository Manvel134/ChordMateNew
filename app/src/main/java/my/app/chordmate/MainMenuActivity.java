package my.app.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button practiceButton = findViewById(R.id.practice_button);
        Button chordsButton = findViewById(R.id.chords_button);
        Button quitButton = findViewById(R.id.quit_button);


        practiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        chordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ChordsActivity.class);
            startActivity(intent);
        });



        quitButton.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

    }
}
