package my.app.chordmate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView totalQuestionsTextView, questionTextView, currentQuestionTextView;
    ImageView chordImageView;
    Button ansA, ansB, ansC, ansD, submitBtn, mainMenuBtn, playAudioBtn;
    MediaPlayer mediaPlayer;
    ProgressDialog loadingDialog;

    int score = 0;
    int totalQuestion = 0;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    private List<ChordQuestion> chordQuestions;
    private SupabaseManager supabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Supabase Manager
        supabaseManager = SupabaseManager.getInstance(this);

        totalQuestionsTextView = findViewById(R.id.total_question);
        currentQuestionTextView = findViewById(R.id.current_question);
        questionTextView = findViewById(R.id.question);
        chordImageView = findViewById(R.id.chord_image);
        playAudioBtn = findViewById(R.id.play_chord_audio_btn);

        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        submitBtn = findViewById(R.id.submit_btn);
        mainMenuBtn = findViewById(R.id.main_menu_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        playAudioBtn.setOnClickListener(v -> playChordAudio());

        mainMenuBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });

        // Show loading dialog
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading chord data...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        // Load data from Supabase
        loadChordData();
    }

    private void loadChordData() {
        supabaseManager.loadChordData(new SupabaseManager.DataLoadCallback() {
            @Override
            public void onDataLoaded(final List<ChordQuestion> questions) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    chordQuestions = questions;
                    totalQuestion = chordQuestions.size();
                    totalQuestionsTextView.setText("Total questions: " + totalQuestion);
                    updateQuestionNumber();
                    loadNewQuestion();
                });
            }

            @Override
            public void onError(final String errorMessage) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    // Fallback to local data or show error
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Error")
                            .setMessage("Failed to load chord data: " + errorMessage)
                            .setPositiveButton("Retry", (dialog, which) -> loadChordData())
                            .setNegativeButton("Exit", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        Button clickedButton = (Button) view;

        if (clickedButton.getId() == R.id.submit_btn) {
            if (selectedAnswer.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setMessage("Please select an answer before submitting!")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            String correctAnswer = chordQuestions.get(currentQuestionIndex).getCorrectAnswer();

            Button[] buttons = {ansA, ansB, ansC, ansD};
            for (Button btn : buttons) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    btn.setBackgroundColor(Color.GREEN);
                } else if (btn.getText().toString().equals(selectedAnswer)) {
                    btn.setBackgroundColor(Color.RED);
                }
                btn.setEnabled(false);
            }

            if (selectedAnswer.equals(correctAnswer)) {
                score++;
            }

            new android.os.Handler().postDelayed(() -> {
                currentQuestionIndex++;
                updateQuestionNumber();
                loadNewQuestion();
            }, 2000);
        } else {
            selectedAnswer = clickedButton.getText().toString();
            ansA.setBackgroundColor(Color.BLACK);
            ansB.setBackgroundColor(Color.BLACK);
            ansC.setBackgroundColor(Color.BLACK);
            ansD.setBackgroundColor(Color.BLACK);
            clickedButton.setBackgroundColor(Color.GRAY);
        }
    }

    void loadNewQuestion() {
        if (currentQuestionIndex >= totalQuestion) {
            finishQuiz();
            return;
        }

        ansA.setBackgroundColor(Color.BLACK);
        ansB.setBackgroundColor(Color.BLACK);
        ansC.setBackgroundColor(Color.BLACK);
        ansD.setBackgroundColor(Color.BLACK);

        ansA.setEnabled(true);
        ansB.setEnabled(true);
        ansC.setEnabled(true);
        ansD.setEnabled(true);

        selectedAnswer = "";
        submitBtn.setText("Submit");

        ChordQuestion currentQuestion = chordQuestions.get(currentQuestionIndex);
        questionTextView.setText(currentQuestion.getQuestion());

        // Load image from Supabase
        supabaseManager.loadImageIntoView(currentQuestion.getImageUrl(), chordImageView);

        // Set answer choices
        String[] choices = currentQuestion.getChoices();
        ansA.setText(choices[0]);
        ansB.setText(choices[1]);
        ansC.setText(choices[2]);
        ansD.setText(choices[3]);
    }

    void playChordAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (currentQuestionIndex >= totalQuestion) {
            return;
        }

        final String audioUrl = chordQuestions.get(currentQuestionIndex).getAudioUrl();
        if (audioUrl == null || audioUrl.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("Audio resource not found!")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Show loading while preparing audio
        Toast.makeText(this, "Loading audio...", Toast.LENGTH_SHORT).show();

        supabaseManager.prepareAudioPlayer(audioUrl, new SupabaseManager.AudioPreparedCallback() {
            @Override
            public void onAudioPrepared(MediaPlayer preparedPlayer) {
                runOnUiThread(() -> {
                    mediaPlayer = preparedPlayer;
                    mediaPlayer.setOnCompletionListener(mp -> {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });
                    mediaPlayer.start();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Error loading audio: " + errorMessage)
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    void finishQuiz() {
        String passStatus = score > totalQuestion * 0.60 ? "Passed" : "Failed";

        new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Score is " + score + " out of " + totalQuestion)
                .setPositiveButton("Restart", (dialogInterface, i) -> restartQuiz())
                .setNegativeButton("Main Menu", (dialogInterface, i) -> {
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    void restartQuiz() {
        score = 0;
        currentQuestionIndex = 0;
        updateQuestionNumber();
        loadNewQuestion();
    }

    private void updateQuestionNumber() {
        currentQuestionTextView.setText("Question: " + (currentQuestionIndex + 1));
    }
}