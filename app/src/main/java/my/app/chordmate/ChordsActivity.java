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

        setupChordCard(R.id.am_card, R.drawable.am_chord, "A Minor (Am)", "A minor chord consists of the notes A, C, and E. It's one of the most common minor chords used in popular music.");
        setupChordCard(R.id.em_card, R.drawable.em_chord, "E Minor (Em)", "E minor uses the notes E, G, and B. This chord has a melancholic sound and is very popular in rock and folk music.");
        setupChordCard(R.id.d_card, R.drawable.d_chord, "D Major (D)", "D major contains D, F#, and A notes. It's a bright-sounding chord commonly used in country, folk, and pop music.");
        setupChordCard(R.id.dm_card, R.drawable.dm_chord, "D Minor (Dm)", "D minor contains D, F, and A notes, creating a soft, somber sound that works well in emotional ballads.");
        setupChordCard(R.id.e_card, R.drawable.e_chord, "E Major (E)", "E major consists of E, G#, and B notes. It's a bright, resonant chord that's fundamental to many rock songs.");
        setupChordCard(R.id.g_card, R.drawable.g_chord, "G Major (G)", "G major contains G, B, and D notes. It's one of the easiest chords to play and is common in folk, country, and pop music.");

        findViewById(R.id.back_to_menu_button).setOnClickListener(v -> finish());
    }

    private void setupChordCard(int cardId, int imageResId, String chordName, String chordDescription) {
        CardView chordCard = findViewById(cardId);
        chordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChordsActivity.this, FullscreenChordActivity.class);
                intent.putExtra("chord_image", imageResId);
                intent.putExtra("chord_name", chordName);
                intent.putExtra("chord_description", chordDescription);
                startActivity(intent);
            }
        });
    }
}