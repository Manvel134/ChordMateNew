package my.app.chordmate;

import com.example.chordmate.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionAnswer {

    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }

    // Beginner Level Questions (Basic Major and Minor Chords)
    public static String beginnerQuestions[] = {
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?"
    };

    public static int beginnerAudios[] = {
            R.raw.c_audio,
            R.raw.g_audio,
            R.raw.am_audio,
            R.raw.em_audio,
            R.raw.d_audio,
            R.raw.f_audio,
            R.raw.a_audio,
            R.raw.e_audio,
            R.raw.dm_audio,
            R.raw.bm_audio
    };

    public static String beginnerChoices[][] = {
            {"C", "G", "Am", "F"},
            {"G", "D", "Em", "C"},
            {"Am", "A", "Em", "Dm"},
            {"Em", "E", "Am", "Bm"},
            {"D", "Dm", "G", "A"},
            {"F", "C", "Bb", "Gm"},
            {"A", "Am", "E", "D"},
            {"E", "Em", "A", "B"},
            {"Dm", "D", "Am", "F"},
            {"Bm", "B", "Em", "F#m"}
    };

    public static String beginnerCorrectAnswers[] = {
            "C", "G", "Am", "Em", "D", "F", "A", "E", "Dm", "Bm"
    };

    public static int beginnerImages[] = {
            R.drawable.c_chord,
            R.drawable.g_chord,
            R.drawable.am_chord,
            R.drawable.em_chord,
            R.drawable.d_chord,
            R.drawable.f_chord,
            R.drawable.a_chord,
            R.drawable.e_chord,
            R.drawable.dm_chord,
            R.drawable.bm_chord
    };

    // Intermediate Level Questions (7th chords, barre chords, less common majors/minors)
    public static String intermediateQuestions[] = {
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?"
    };

    public static int intermediateAudios[] = {
            R.raw.a7_audio,
            R.raw.d7_audio,
            R.raw.e7_audio,
            R.raw.g7_audio,
            R.raw.c7_audio,
            R.raw.b7_audio,
            R.raw.f_sharp_m_audio,
            R.raw.c_sharp_m_audio,
            R.raw.g_sharp_m_audio,
            R.raw.bb_audio,
            R.raw.am7_audio,
            R.raw.cmaj7_audio
    };

    public static String intermediateChoices[][] = {
            {"A7", "A", "Am7", "Amaj7"},
            {"D7", "D", "Dm7", "Dmaj7"},
            {"E7", "E", "Em7", "Emaj7"},
            {"G7", "G", "Gm7", "Gmaj7"},
            {"C7", "C", "Cm7", "Cmaj7"},
            {"B7", "B", "Bm7", "Bmaj7"},
            {"F#m", "F#", "F#m7", "F#maj7"},
            {"C#m", "C#", "C#m7", "C#maj7"},
            {"G#m", "G#", "G#m7", "G#maj7"},
            {"Bb", "B", "Bbm", "Bb7"},
            {"Am7", "Am", "A7", "Amaj7"},
            {"Cmaj7", "C", "C7", "Cm7"}
    };

    public static String intermediateCorrectAnswers[] = {
            "A7", "D7", "E7", "G7", "C7", "B7", "F#m", "C#m", "G#m", "Bb", "Am7", "Cmaj7"
    };

    public static int intermediateImages[] = {
            R.drawable.a7_chord,
            R.drawable.d7_chord,
            R.drawable.e7_chord,
            R.drawable.g7_chord,
            R.drawable.c7_chord,
            R.drawable.b7_chord,
            R.drawable.f_sharp_m_chord,
            R.drawable.c_sharp_m_chord,
            R.drawable.g_sharp_m_chord,
            R.drawable.bb_chord,
            R.drawable.am7_chord,
            R.drawable.cmaj7_chord
    };

    // Advanced Level Questions (Extended chords, complex voicings)
    public static String advancedQuestions[] = {
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?"
    };

    public static int advancedAudios[] = {
            R.raw.amaj7_audio,
            R.raw.dmaj7_audio,
            R.raw.d6_audio,
            R.raw.g6_audio,
            R.raw.esus4_audio,
            R.raw.am7_audio,
            R.raw.cmaj7_audio,
            R.raw.b7_audio
    };

    public static String advancedChoices[][] = {
            {"Amaj7", "A7", "Am7", "A"},
            {"Dmaj7", "D7", "Dm7", "D"},
            {"D6", "D", "Dm6", "D7"},
            {"G6", "G", "Gm6", "G7"},
            {"Esus4", "E", "Em", "E7"},
            {"Am7", "A7", "Amaj7", "Am"},
            {"Cmaj7", "C7", "Cm7", "C"},
            {"B7", "Bmaj7", "Bm7", "B"}
    };

    public static String advancedCorrectAnswers[] = {
            "Amaj7", "Dmaj7", "D6", "G6", "Esus4", "Am7", "Cmaj7", "B7"
    };

    public static int advancedImages[] = {
            R.drawable.amaj7_chord,
            R.drawable.dmaj7_chord,
            R.drawable.d6_chord,
            R.drawable.g6_chord,
            R.drawable.esus4_chord,
            R.drawable.am7_chord,
            R.drawable.cmaj7_chord,
            R.drawable.b7_chord
    };

    // Expert Level Questions (Mixed difficulty with tricky options)
    public static String expertQuestions[] = {
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?",
            "What chord is this?"
    };

    public static int expertAudios[] = {
            R.raw.f_sharp_m_audio,
            R.raw.bb_audio,
            R.raw.g_sharp_m_audio,
            R.raw.c_sharp_m_audio,
            R.raw.amaj7_audio,
            R.raw.dmaj7_audio,
            R.raw.esus4_audio,
            R.raw.d6_audio,
            R.raw.b7_audio,
            R.raw.g6_audio
    };

    public static String expertChoices[][] = {
            {"F#m", "Fm", "F#", "F#7"},
            {"Bb", "B", "A#", "Bbm"},
            {"G#m", "Gm", "G#", "G#7"},
            {"C#m", "Cm", "C#", "C#7"},
            {"Amaj7", "A7", "Am7", "Aadd9"},
            {"Dmaj7", "D7", "Dm7", "Dadd9"},
            {"Esus4", "Esus2", "E", "Eadd9"},
            {"D6", "Dadd9", "D", "Dm6"},
            {"B7", "Bmaj7", "B", "Bsus4"},
            {"G6", "Gadd9", "G", "Gm6"}
    };

    public static String expertCorrectAnswers[] = {
            "F#m", "Bb", "G#m", "C#m", "Amaj7", "Dmaj7", "Esus4", "D6", "B7", "G6"
    };

    public static int expertImages[] = {
            R.drawable.f_sharp_m_chord,
            R.drawable.bb_chord,
            R.drawable.g_sharp_m_chord,
            R.drawable.c_sharp_m_chord,
            R.drawable.amaj7_chord,
            R.drawable.dmaj7_chord,
            R.drawable.esus4_chord,
            R.drawable.d6_chord,
            R.drawable.b7_chord,
            R.drawable.g6_chord
    };

    // Utility methods to get questions by difficulty level
    public static List<QuizQuestion> getQuestionsByDifficulty(DifficultyLevel level) {
        List<QuizQuestion> questions = new ArrayList<>();

        switch (level) {
            case BEGINNER:
                for (int i = 0; i < beginnerQuestions.length; i++) {
                    questions.add(new QuizQuestion(
                            beginnerQuestions[i],
                            beginnerChoices[i],
                            beginnerCorrectAnswers[i],
                            beginnerImages[i],
                            beginnerAudios[i],
                            level
                    ));
                }
                break;
            case INTERMEDIATE:
                for (int i = 0; i < intermediateQuestions.length; i++) {
                    questions.add(new QuizQuestion(
                            intermediateQuestions[i],
                            intermediateChoices[i],
                            intermediateCorrectAnswers[i],
                            intermediateImages[i],
                            intermediateAudios[i],
                            level
                    ));
                }
                break;
            case ADVANCED:
                for (int i = 0; i < advancedQuestions.length; i++) {
                    questions.add(new QuizQuestion(
                            advancedQuestions[i],
                            advancedChoices[i],
                            advancedCorrectAnswers[i],
                            advancedImages[i],
                            advancedAudios[i],
                            level
                    ));
                }
                break;
            case EXPERT:
                for (int i = 0; i < expertQuestions.length; i++) {
                    questions.add(new QuizQuestion(
                            expertQuestions[i],
                            expertChoices[i],
                            expertCorrectAnswers[i],
                            expertImages[i],
                            expertAudios[i],
                            level
                    ));
                }
                break;
        }

        // Shuffle the questions for variety
        Collections.shuffle(questions);
        return questions;
    }

    public static List<QuizQuestion> getMixedQuestions(int count) {
        List<QuizQuestion> allQuestions = new ArrayList<>();

        // Add questions from all difficulty levels
        allQuestions.addAll(getQuestionsByDifficulty(DifficultyLevel.BEGINNER));
        allQuestions.addAll(getQuestionsByDifficulty(DifficultyLevel.INTERMEDIATE));
        allQuestions.addAll(getQuestionsByDifficulty(DifficultyLevel.ADVANCED));
        allQuestions.addAll(getQuestionsByDifficulty(DifficultyLevel.EXPERT));

        Collections.shuffle(allQuestions);

        // Return requested number of questions or all if count is larger
        return allQuestions.subList(0, Math.min(count, allQuestions.size()));
    }

    // Inner class to represent a quiz question
    public static class QuizQuestion {
        private String question;
        private String[] choices;
        private String correctAnswer;
        private int imageResourceId;
        private int audioResourceId;
        private DifficultyLevel difficulty;

        public QuizQuestion(String question, String[] choices, String correctAnswer,
                            int imageResourceId, int audioResourceId, DifficultyLevel difficulty) {
            this.question = question;
            this.choices = choices;
            this.correctAnswer = correctAnswer;
            this.imageResourceId = imageResourceId;
            this.audioResourceId = audioResourceId;
            this.difficulty = difficulty;
        }

        // Getters
        public String getQuestion() { return question; }
        public String[] getChoices() { return choices; }
        public String getCorrectAnswer() { return correctAnswer; }
        public int getImageResourceId() { return imageResourceId; }
        public int getAudioResourceId() { return audioResourceId; }
        public DifficultyLevel getDifficulty() { return difficulty; }
    }

    // Legacy support - keep original arrays for backward compatibility
    public static String question[] = beginnerQuestions;
    public static int audios[] = beginnerAudios;
    public static String choices[][] = beginnerChoices;
    public static String correctAnswers[] = beginnerCorrectAnswers;
    public static int images[] = beginnerImages;
}