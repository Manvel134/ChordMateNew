package my.app.chordmate;

/**
 * Represents a guitar chord with all its details
 */
public class ChordItem {
    private String name;
    private String description;
    private String imageUrl;
    private String audioUrl;
    private int localImageResId; // Fallback local resource ID

    public ChordItem(String name, String description, String imageUrl, String audioUrl, int localImageResId) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.localImageResId = localImageResId;
    }

    // Constructor for Supabase data without local image resource
    public ChordItem(String name, String description, String imageUrl, String audioUrl) {
        this(name, description, imageUrl, audioUrl, 0);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public int getLocalImageResId() {
        return localImageResId;
    }

    public void setLocalImageResId(int localImageResId) {
        this.localImageResId = localImageResId;
    }
}