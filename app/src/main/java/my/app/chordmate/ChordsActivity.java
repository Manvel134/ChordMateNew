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

        // Basic Major Chords
        setupChordCard(R.id.a_card, R.drawable.a_chord, "A Major (A)", "A major contains A, C#, and E notes. It's a bright, happy chord that's essential in many genres including rock, country, and folk music.");
        setupChordCard(R.id.bb_card, R.drawable.bb_chord, "B Flat Major (Bb)", "B flat major contains Bb, D, and F notes. It's a warm, rich chord commonly used in jazz, blues, and many pop songs.");
        setupChordCard(R.id.c_card, R.drawable.c_chord, "C Major (C)", "C major contains C, E, and G notes. Often called the 'natural' chord, it's one of the first chords beginners learn and forms the foundation of many songs.");
        setupChordCard(R.id.d_card, R.drawable.d_chord, "D Major (D)", "D major contains D, F#, and A notes. It's a bright-sounding chord commonly used in country, folk, and pop music.");
        setupChordCard(R.id.e_card, R.drawable.e_chord, "E Major (E)", "E major consists of E, G#, and B notes. It's a bright, resonant chord that's fundamental to many rock songs.");
        setupChordCard(R.id.f_card, R.drawable.f_chord, "F Major (F)", "F major consists of F, A, and C notes. Though challenging for beginners due to the barre technique, it's crucial for playing in many keys and adds richness to chord progressions.");
        setupChordCard(R.id.g_card, R.drawable.g_chord, "G Major (G)", "G major contains G, B, and D notes. It's one of the easiest chords to play and is common in folk, country, and pop music.");

        // Minor Chords
        setupChordCard(R.id.am_card, R.drawable.am_chord, "A Minor (Am)", "A minor chord consists of the notes A, C, and E. It's one of the most common minor chords used in popular music.");
        setupChordCard(R.id.bm_card, R.drawable.bm_chord, "B Minor (Bm)", "B minor uses the notes B, D, and F#. This chord has a deep, emotional quality and is popular in ballads and rock music.");
        setupChordCard(R.id.c_sharp_m_card, R.drawable.c_sharp_m_chord, "C# Minor (C#m)", "C# minor contains C#, E, and G# notes. This chord has a dark, mysterious quality and is commonly used in rock and alternative music.");
        setupChordCard(R.id.dm_card, R.drawable.dm_chord, "D Minor (Dm)", "D minor contains D, F, and A notes, creating a soft, somber sound that works well in emotional ballads.");
        setupChordCard(R.id.em_card, R.drawable.em_chord, "E Minor (Em)", "E minor uses the notes E, G, and B. This chord has a melancholic sound and is very popular in rock and folk music.");
        setupChordCard(R.id.f_sharp_m_card, R.drawable.f_sharp_m_chord, "F# Minor (F#m)", "F# minor contains F#, A, and C# notes. This barre chord has a rich, full sound and is essential for many rock and pop progressions.");
        setupChordCard(R.id.g_sharp_m_card, R.drawable.g_sharp_m_chord, "G# Minor (G#m)", "G# minor uses G#, B, and D# notes. This barre chord creates a dark, intense mood and is popular in rock and alternative genres.");

        // Seventh Chords
        setupChordCard(R.id.a7_card, R.drawable.a7_chord, "A Dominant 7th (A7)", "A7 contains A, C#, E, and G notes. This bluesy chord creates tension that resolves nicely to D major, making it essential in blues and country music.");
        setupChordCard(R.id.am7_card, R.drawable.am7_chord, "A Minor 7th (Am7)", "Am7 contains A, C, E, and G notes. This smooth, jazzy chord is perfect for creating a relaxed, sophisticated sound in ballads and jazz progressions.");
        setupChordCard(R.id.amaj7_card, R.drawable.amaj7_chord, "A Major 7th (Amaj7)", "Amaj7 contains A, C#, E, and G# notes. This dreamy, ethereal chord adds sophistication and is commonly used in jazz, R&B, and neo-soul music.");
        setupChordCard(R.id.b7_card, R.drawable.b7_chord, "B Dominant 7th (B7)", "B7 contains B, D#, F#, and A notes. This chord creates strong tension that resolves to E major, making it crucial in blues progressions and key changes.");
        setupChordCard(R.id.c7_card, R.drawable.c7_chord, "C Dominant 7th (C7)", "C7 contains C, E, G, and Bb notes. This bluesy chord is fundamental in blues, jazz, and country music, often used to transition between chords.");
        setupChordCard(R.id.cmaj7_card, R.drawable.cmaj7_chord, "C Major 7th (Cmaj7)", "Cmaj7 contains C, E, G, and B notes. This beautiful, open chord creates a dreamy, sophisticated sound perfect for ballads and jazz standards.");
        setupChordCard(R.id.d7_card, R.drawable.d7_chord, "D Dominant 7th (D7)", "D7 contains D, F#, A, and C notes. This seventh chord creates tension that naturally resolves to G major, making it essential in blues and folk music.");
        setupChordCard(R.id.dmaj7_card, R.drawable.dmaj7_chord, "D Major 7th (Dmaj7)", "Dmaj7 contains D, F#, A, and C# notes. This warm, rich chord adds color and sophistication to progressions in folk, country, and jazz music.");
        setupChordCard(R.id.e7_card, R.drawable.e7_chord, "E Dominant 7th (E7)", "E7 contains E, G#, B, and D notes. This powerful chord is a staple in blues and rock music, creating strong resolution to A major or minor.");
        setupChordCard(R.id.g7_card, R.drawable.g7_chord, "G Dominant 7th (G7)", "G7 contains G, B, D, and F notes. This chord creates nice tension that resolves to C major, making it essential in blues, jazz, and folk progressions.");

        // Extended and Sus Chords
        setupChordCard(R.id.d6_card, R.drawable.d6_chord, "D Major 6th (D6)", "D6 contains D, F#, A, and B notes. This sweet, open chord adds a folk or country flavor and is often used as a substitute for D major.");
        setupChordCard(R.id.esus4_card, R.drawable.esus4_chord, "E Suspended 4th (Esus4)", "Esus4 contains E, A, and B notes. This suspended chord creates tension and anticipation, often used before resolving to E major for dramatic effect.");
        setupChordCard(R.id.g6_card, R.drawable.g6_chord, "G Major 6th (G6)", "G6 contains G, B, D, and E notes. This bright, cheerful chord is perfect for ending songs or adding a folk-country flavor to progressions.");

        findViewById(R.id.back_to_menu_button).setOnClickListener(v -> finish());
    }

    private void setupChordCard(int cardId, int imageResId, String chordName, String chordDescription) {
        CardView chordCard = findViewById(cardId);
        if (chordCard != null) {
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
}