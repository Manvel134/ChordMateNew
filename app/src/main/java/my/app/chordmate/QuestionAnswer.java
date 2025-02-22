package my.app.chordmate;

import com.example.chordmate.R;

public class QuestionAnswer {

    public static String question[] = {
            "What chord is this?",
            "What chord is this?",
            "What chord is this?"
    };

    public static int audios[] = {
            R.raw.am_audio,
            R.raw.em_audio,
            R.raw.d_audio
    };


    public static String choices[][] = {
            {"Am", "A", "Am7", "Em"},
            {"E", "E7", "Em", "Emaj"},
            {"Dm", "D9", "Dmaj", "D"}
    };

    public static String correctAnswers[] = {
            "Am",
            "Em",
            "D"
    };

    public static int images[] = {
            R.drawable.am_chord,
            R.drawable.em_chord,
            R.drawable.d_chord
    };
}
