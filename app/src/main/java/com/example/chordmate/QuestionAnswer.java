package com.example.chordmate;

public class QuestionAnswer {

    public static String question[] = {
            "What chord is this?",
            "What chord is this?",
            "What chord is this?"
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
