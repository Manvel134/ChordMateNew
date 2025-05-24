package my.app.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.chordmate.R;

public class DifficultySelection extends AppCompatActivity {

    private CardView beginnerCard, intermediateCard, advancedCard, expertCard, mixedCard;
    private TextView beginnerDesc, intermediateDesc, advancedDesc, expertDesc, mixedDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.difficulty_selection);

        initializeViews();
        setupClickListeners();
        setupDescriptions();
    }

    private void initializeViews() {
        beginnerCard = findViewById(R.id.beginner_card);
        intermediateCard = findViewById(R.id.intermediate_card);
        advancedCard = findViewById(R.id.advanced_card);
        expertCard = findViewById(R.id.expert_card);
        mixedCard = findViewById(R.id.mixed_card);

        beginnerDesc = findViewById(R.id.beginner_description);
        intermediateDesc = findViewById(R.id.intermediate_description);
        advancedDesc = findViewById(R.id.advanced_description);
        expertDesc = findViewById(R.id.expert_description);
        mixedDesc = findViewById(R.id.mixed_description);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void setupDescriptions() {
        beginnerDesc.setText("Basic major and minor chords (C, G, Am, Em, D, F, A, E, Dm, Bm)\n10 Questions");
        intermediateDesc.setText("7th chords, barre chords, and less common chords (A7, F#m, Bb, etc.)\n12 Questions");
        advancedDesc.setText("Extended chords, suspended chords, and complex voicings (Maj7, Sus4, 6th chords)\n8 Questions");
        expertDesc.setText("Mixed difficulty with tricky answer choices and advanced chord recognition\n10 Questions");
        mixedDesc.setText("Random selection from all difficulty levels for comprehensive practice\n15 Questions");
    }

    private void setupClickListeners() {
        beginnerCard.setOnClickListener(v -> startQuiz(QuestionAnswer.DifficultyLevel.BEGINNER));
        intermediateCard.setOnClickListener(v -> startQuiz(QuestionAnswer.DifficultyLevel.INTERMEDIATE));
        advancedCard.setOnClickListener(v -> startQuiz(QuestionAnswer.DifficultyLevel.ADVANCED));
        expertCard.setOnClickListener(v -> startQuiz(QuestionAnswer.DifficultyLevel.EXPERT));
        mixedCard.setOnClickListener(v -> startMixedQuiz());
    }

    private void startQuiz(QuestionAnswer.DifficultyLevel difficulty) {
        Intent intent = new Intent(DifficultySelection.this, MainActivity.class);
        intent.putExtra("difficulty_level", difficulty.name());
        intent.putExtra("quiz_type", "difficulty");
        startActivity(intent);
    }

    private void startMixedQuiz() {
        Intent intent = new Intent(DifficultySelection.this, MainActivity.class);
        intent.putExtra("quiz_type", "mixed");
        intent.putExtra("question_count", 15);
        startActivity(intent);
    }
}