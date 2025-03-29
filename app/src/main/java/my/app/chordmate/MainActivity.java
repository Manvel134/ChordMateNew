package my.app.chordmate;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView totalQuestionsTextView, questionTextView, currentQuestionTextView;
    ImageView chordImageView;
    Button ansA, ansB, ansC, ansD, submitBtn, mainMenuBtn, playAudioBtn;
    MediaPlayer mediaPlayer;

    int score = 0;
    int totalQuestion = QuestionAnswer.question.length;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        totalQuestionsTextView.setText("Total questions: " + totalQuestion);
        updateQuestionNumber();
        loadNewQuestion();
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

            String correctAnswer = QuestionAnswer.correctAnswers[currentQuestionIndex];

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

        questionTextView.setText(QuestionAnswer.question[currentQuestionIndex]);
        chordImageView.setImageResource(QuestionAnswer.images[currentQuestionIndex]);
        ansA.setText(QuestionAnswer.choices[currentQuestionIndex][0]);
        ansB.setText(QuestionAnswer.choices[currentQuestionIndex][1]);
        ansC.setText(QuestionAnswer.choices[currentQuestionIndex][2]);
        ansD.setText(QuestionAnswer.choices[currentQuestionIndex][3]);
    }

    void playChordAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (currentQuestionIndex >= QuestionAnswer.audios.length || QuestionAnswer.audios[currentQuestionIndex] == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Audio resource not found!")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        mediaPlayer = MediaPlayer.create(this, QuestionAnswer.audios[currentQuestionIndex]);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Error loading audio file!")
                    .setPositiveButton("OK", null)
                    .show();
        }
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