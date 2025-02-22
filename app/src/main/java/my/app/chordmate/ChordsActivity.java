package my.app.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.chordmate.R;

public class ChordsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chords);

        setupChordCard(R.id.am_card, R.drawable.am_chord, "A Minor (Am)");
        setupChordCard(R.id.em_card, R.drawable.em_chord, "E Minor (Em)");
        setupChordCard(R.id.d_card, R.drawable.d_chord, "D Major (D)");
        setupChordCard(R.id.dm_card, R.drawable.dm_chord, "D Minor (Dm)");
        setupChordCard(R.id.e_card, R.drawable.e_chord, "E Major (E)");
        setupChordCard(R.id.g_card, R.drawable.g_chord, "G Major (G)");

        findViewById(R.id.back_to_menu_button).setOnClickListener(v -> finish());
    }

    private void setupChordCard(int cardId, int imageResId, String chordName) {
        CardView chordCard = findViewById(cardId);
        chordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChordsActivity.this, FullscreenChordActivity.class);
                intent.putExtra("chord_image", imageResId);
                intent.putExtra("chord_name", chordName);
                startActivity(intent);
            }
        });
    }
}
