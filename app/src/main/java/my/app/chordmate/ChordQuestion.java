package my.app.chordmate;

public class ChordQuestion {
    private String question;
    private String[] choices;
    private String correctAnswer;
    private String imageUrl;
    private String audioUrl;

    public ChordQuestion(String question, String[] choices, String correctAnswer,
                         String imageUrl, String audioUrl) {
        this.question = question;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getChoices() {
        return choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
}