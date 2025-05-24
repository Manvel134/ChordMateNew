package my.app.chordmate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chordmate.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView totalQuestionsTextView, questionTextView, currentQuestionTextView, scoreTextView;
    ImageView chordImageView;
    ImageButton backButton;
    MaterialButton ansA, ansB, ansC, ansD, submitBtn, playAudioBtn;
    ProgressBar questionProgressBar;
    MediaPlayer mediaPlayer;
    ProgressDialog loadingDialog;

    int score = 0;
    int totalQuestion = 0;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    private final int[] buttonColors = {
            R.color.answer_button_a,
            R.color.answer_button_a,
            R.color.answer_button_a,
            R.color.answer_button_a
    };

    // Support both local QuestionAnswer data and Supabase data
    private List<ChordQuestion> chordQuestions; // For Supabase data
    private List<QuestionAnswer.QuizQuestion> quizQuestions; // For local data
    private SupabaseManager supabaseManager;

    // Quiz configuration
    private boolean useLocalData = false;
    private String quizType = "";
    private QuestionAnswer.DifficultyLevel difficultyLevel = null;
    private int questionCount = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Supabase Manager
        supabaseManager = SupabaseManager.getInstance(this);

        // Get intent extras for quiz configuration
        Intent intent = getIntent();
        quizType = intent.getStringExtra("quiz_type");
        questionCount = intent.getIntExtra("question_count", 15);

        String difficultyString = intent.getStringExtra("difficulty_level");
        if (difficultyString != null) {
            try {
                difficultyLevel = QuestionAnswer.DifficultyLevel.valueOf(difficultyString);
            } catch (IllegalArgumentException e) {
                difficultyLevel = QuestionAnswer.DifficultyLevel.BEGINNER;
            }
        }

        // Find all views by ID
        totalQuestionsTextView = findViewById(R.id.total_question);
        currentQuestionTextView = findViewById(R.id.current_question);
        scoreTextView = findViewById(R.id.score_text);
        questionTextView = findViewById(R.id.question);
        chordImageView = findViewById(R.id.chord_image);
        playAudioBtn = findViewById(R.id.play_chord_audio_btn);
        questionProgressBar = findViewById(R.id.question_progress);
        backButton = findViewById(R.id.back_button);

        // Buttons are now MaterialButtons
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        submitBtn = findViewById(R.id.submit_btn);

        // Set click listeners
        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        playAudioBtn.setOnClickListener(v -> playChordAudio());
        backButton.setOnClickListener(v -> showExitConfirmation());

        // Initialize score text
        updateScoreText();

        // Determine data source and load questions
        if (quizType != null && !quizType.isEmpty()) {
            // Use local QuestionAnswer data
            useLocalData = true;
            loadLocalQuestionData();
        } else {
            // Use Supabase data (original behavior)
            useLocalData = false;
            // Show loading dialog
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage("Loading chord data...");
            loadingDialog.setCancelable(false);
            loadingDialog.show();
            loadChordData();
        }
    }

    private void loadLocalQuestionData() {
        try {
            if ("mixed".equals(quizType)) {
                quizQuestions = QuestionAnswer.getMixedQuestions(questionCount);
            } else if ("difficulty".equals(quizType) && difficultyLevel != null) {
                quizQuestions = QuestionAnswer.getQuestionsByDifficulty(difficultyLevel);
            } else {
                // Fallback to beginner questions
                quizQuestions = QuestionAnswer.getQuestionsByDifficulty(QuestionAnswer.DifficultyLevel.BEGINNER);
            }

            totalQuestion = quizQuestions.size();
            totalQuestionsTextView.setText("Total Questions: " + totalQuestion);
            updateQuestionNumber();
            updateProgressBar();
            loadNewQuestion();

        } catch (Exception e) {
            Toast.makeText(this, "Error loading questions: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to return to difficulty selection?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Check if we came from difficulty selection
                    if (isFromDifficultySelection()) {
                        // Go back to difficulty selection
                        Intent intent = new Intent(MainActivity.this, DifficultySelection.class);
                        startActivity(intent);
                    } else {
                        // Go back to main menu (for Supabase data or other sources)
                        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                    }
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private boolean isFromDifficultySelection() {
        // If we have local data (quiz_type is set), it means we came from difficulty selection
        return useLocalData && (quizType != null && !quizType.isEmpty());
    }

    private void updateScoreText() {
        scoreTextView.setText("Score: " + score);
    }

    private void loadChordData() {
        supabaseManager.loadChordData(new SupabaseManager.DataLoadCallback() {
            @Override
            public void onDataLoaded(final List<ChordQuestion> questions) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    chordQuestions = questions;
                    totalQuestion = chordQuestions.size();
                    totalQuestionsTextView.setText("Total Questions: " + totalQuestion);
                    updateQuestionNumber();
                    updateProgressBar();
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
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Clean up the SupabaseManager resources
        if (supabaseManager != null) {
            supabaseManager.cleanup();
        }
    }

    // Add this method to preload the next question's media (for Supabase data only)
    private void preloadNextQuestion() {
        if (!useLocalData && currentQuestionIndex + 1 < totalQuestion) {
            ChordQuestion nextQuestion = chordQuestions.get(currentQuestionIndex + 1);

            // Preload the next question's image into Glide's cache
            String imageUrl = supabaseManager.getFullStorageUrl(nextQuestion.getImageUrl());
            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload();
        }
    }

    // Update progress bar based on current question
    private void updateProgressBar() {
        if (totalQuestion > 0) {
            int progress = (currentQuestionIndex * 100) / totalQuestion;
            questionProgressBar.setProgress(progress);
        }
    }

    // Reset button colors to their original colors
    private void resetButtonColors() {
        ansA.setBackgroundColor(getResources().getColor(buttonColors[0]));
        ansB.setBackgroundColor(getResources().getColor(buttonColors[1]));
        ansC.setBackgroundColor(getResources().getColor(buttonColors[2]));
        ansD.setBackgroundColor(getResources().getColor(buttonColors[3]));
    }

    void loadNewQuestion() {
        if (currentQuestionIndex >= totalQuestion) {
            finishQuiz();
            return;
        }

        // Reset button colors
        resetButtonColors();

        ansA.setEnabled(true);
        ansB.setEnabled(true);
        ansC.setEnabled(true);
        ansD.setEnabled(true);

        selectedAnswer = "";
        submitBtn.setText("Submit");

        if (useLocalData) {
            // Load from local QuestionAnswer data
            QuestionAnswer.QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestion());

            // Load image from resources
            chordImageView.setImageResource(currentQuestion.getImageResourceId());

            // Set answer choices
            String[] choices = currentQuestion.getChoices();
            ansA.setText(choices[0]);
            ansB.setText(choices[1]);
            ansC.setText(choices[2]);
            ansD.setText(choices[3]);
        } else {
            // Load from Supabase data (original behavior)
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

            // Preload the next question's media
            preloadNextQuestion();
        }

        // Update progress
        updateProgressBar();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.submit_btn) {
            if (selectedAnswer.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setMessage("Please select an answer before submitting!")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            String correctAnswer;
            if (useLocalData) {
                correctAnswer = quizQuestions.get(currentQuestionIndex).getCorrectAnswer();
            } else {
                correctAnswer = chordQuestions.get(currentQuestionIndex).getCorrectAnswer();
            }

            MaterialButton[] buttons = {ansA, ansB, ansC, ansD};
            for (MaterialButton btn : buttons) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    btn.setBackgroundColor(Color.GREEN);
                } else if (btn.getText().toString().equals(selectedAnswer) && !selectedAnswer.equals(correctAnswer)) {
                    btn.setBackgroundColor(Color.RED);
                }
                btn.setEnabled(false);
            }

            if (selectedAnswer.equals(correctAnswer)) {
                score++;
                updateScoreText();
            }

            new android.os.Handler().postDelayed(() -> {
                currentQuestionIndex++;
                // Only update question number if we haven't finished the quiz
                if (currentQuestionIndex < totalQuestion) {
                    updateQuestionNumber();
                }
                loadNewQuestion();
            }, 2000);
        } else {
            // Answer button was clicked
            selectedAnswer = ((MaterialButton) view).getText().toString();

            // Reset all buttons to their original colors
            resetButtonColors();

            // Set clicked button to a highlighted color
            ((MaterialButton) view).setBackgroundColor(Color.DKGRAY);
        }
    }

    void playChordAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (currentQuestionIndex >= totalQuestion) {
            return;
        }

        if (useLocalData) {
            // Play audio from local resources
            QuestionAnswer.QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
            int audioResourceId = currentQuestion.getAudioResourceId();

            try {
                mediaPlayer = MediaPlayer.create(this, audioResourceId);
                if (mediaPlayer != null) {
                    mediaPlayer.setOnCompletionListener(mp -> {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });
                    mediaPlayer.start();
                } else {
                    Toast.makeText(this, "Audio resource not found!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error playing audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Play audio from Supabase (original behavior)
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
    }

    void finishQuiz() {
        String passStatus = score > totalQuestion * 0.60 ? "Passed" : "Failed";

        new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Score is " + score + " out of " + totalQuestion)
                .setPositiveButton("Restart", (dialogInterface, i) -> restartQuiz())
                .setNegativeButton("Back to Selection", (dialogInterface, i) -> {
                    // Check where to go back to
                    if (isFromDifficultySelection()) {
                        // Go back to difficulty selection
                        Intent intent = new Intent(MainActivity.this, DifficultySelection.class);
                        startActivity(intent);
                    } else {
                        // Go back to main menu (for Supabase data or other sources)
                        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                    }
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    void restartQuiz() {
        score = 0;
        currentQuestionIndex = 0;
        updateScoreText();
        updateQuestionNumber();

        // Reshuffle questions if using local data
        if (useLocalData) {
            loadLocalQuestionData();
        } else {
            loadNewQuestion();
        }
    }

    private void updateQuestionNumber() {
        // Ensure we don't exceed the total number of questions
        int displayQuestionNumber = Math.min(currentQuestionIndex + 1, totalQuestion);
        currentQuestionTextView.setText("Question: " + displayQuestionNumber + "/" + totalQuestion);
    }

    @Override
    public void onBackPressed() {
        showExitConfirmation();
    }
}