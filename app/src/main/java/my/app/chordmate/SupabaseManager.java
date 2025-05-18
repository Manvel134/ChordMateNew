package my.app.chordmate;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseManager {
    private static final String TAG = "SupabaseManager";

    // Replace with your Supabase project URL and API Key
    private static final String SUPABASE_URL = "https://ppzvdasaudvwfsrhmeew.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBwenZkYXNhdWR2d2ZzcmhtZWV3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDY5NTA5NjAsImV4cCI6MjA2MjUyNjk2MH0.G093MoP4MHxFY0a01FgLqZ2wWR8W1f2YfUh3f898skA";

    // Storage bucket name where you'll store media files
    private static final String STORAGE_BUCKET = "chordmateapp";

    private static SupabaseManager instance;
    private final Context context;
    private final ExecutorService executorService;

    // Cache for audio files
    private final Map<String, File> audioCache = new HashMap<>();

    // Store preloaded MediaPlayers to avoid recreation
    private final Map<String, MediaPlayer> mediaPlayerCache = new HashMap<>();

    private List<ChordQuestion> chordQuestions = new ArrayList<>();
    private List<ChordItem> chordLibrary = new ArrayList<>();
    private boolean isDataLoaded = false;
    private boolean isChordLibraryLoaded = false;
    private boolean isPreloadingMedia = false;

    private SupabaseManager(Context context) {
        this.context = context.getApplicationContext();
        this.executorService = Executors.newFixedThreadPool(3); // Create a thread pool
    }

    public static synchronized SupabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new SupabaseManager(context);
        }
        return instance;
    }

    public void loadChordLibrary(final ChordLibraryCallback callback) {
        if (isChordLibraryLoaded && !chordLibrary.isEmpty()) {
            callback.onDataLoaded(chordLibrary);
            return;
        }

        executorService.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/chord_library?select=*");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("apikey", SUPABASE_API_KEY);
                connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                connection.setConnectTimeout(5000); // 5 second timeout
                connection.setReadTimeout(10000); // 10 second read timeout

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String jsonResponse = response.toString();

                    // Parse JSON data
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    chordLibrary.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String name = jsonObject.getString("name");
                        String description = jsonObject.getString("description");
                        String imageUrl = jsonObject.getString("image_url");
                        String audioUrl = jsonObject.getString("audio_url");

                        ChordItem chord = new ChordItem(
                                name, description, imageUrl, audioUrl
                        );
                        chordLibrary.add(chord);
                    }

                    isChordLibraryLoaded = true;
                    callback.onDataLoaded(chordLibrary);

                    // Start preloading media in background after data is loaded
                    preloadChordLibraryMedia();

                } else {
                    String errorMsg = "Server error: " + responseCode;
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading chord library", e);
                callback.onError("Error: " + e.getMessage());
            } finally {
                // Clean up resources
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void preloadChordLibraryMedia() {
        if (isPreloadingMedia || chordLibrary.isEmpty()) {
            return;
        }

        isPreloadingMedia = true;

        executorService.execute(() -> {
            try {
                // First, preload all chord images to Glide's cache
                for (ChordItem chord : chordLibrary) {
                    String imageUrl = getFullStorageUrl(chord.getImageUrl());

                    // Preload image to Glide's cache
                    Glide.with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload();
                }

                // Then preload and cache audio files
                for (ChordItem chord : chordLibrary) {
                    String audioUrl = chord.getAudioUrl();
                    preloadAudioFile(audioUrl);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error preloading chord library media", e);
            } finally {
                isPreloadingMedia = false;
            }
        });
    }

    // Existing methods

    public void loadChordData(final DataLoadCallback callback) {
        if (isDataLoaded && !chordQuestions.isEmpty()) {
            callback.onDataLoaded(chordQuestions);
            return;
        }

        executorService.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/chord_questions?select=*");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("apikey", SUPABASE_API_KEY);
                connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                connection.setConnectTimeout(5000); // 5 second timeout
                connection.setReadTimeout(10000); // 10 second read timeout

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String jsonResponse = response.toString();

                    // Parse JSON data
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    chordQuestions.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String question = jsonObject.getString("question");
                        String correctAnswer = jsonObject.getString("correct_answer");
                        String imageUrl = jsonObject.getString("image_url");
                        String audioUrl = jsonObject.getString("audio_url");

                        // Parse choices array
                        JSONArray choicesArray = jsonObject.getJSONArray("choices");
                        String[] choices = new String[choicesArray.length()];
                        for (int j = 0; j < choicesArray.length(); j++) {
                            choices[j] = choicesArray.getString(j);
                        }

                        ChordQuestion chordQuestion = new ChordQuestion(
                                question, choices, correctAnswer, imageUrl, audioUrl
                        );
                        chordQuestions.add(chordQuestion);
                    }

                    isDataLoaded = true;
                    callback.onDataLoaded(chordQuestions);

                    // Start preloading media in background after data is loaded
                    preloadMediaFiles();

                } else {
                    String errorMsg = "Server error: " + responseCode;
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading chord data", e);
                callback.onError("Error: " + e.getMessage());
            } finally {
                // Clean up resources
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void preloadMediaFiles() {
        if (isPreloadingMedia || chordQuestions.isEmpty()) {
            return;
        }

        isPreloadingMedia = true;

        executorService.execute(() -> {
            try {
                // First, preload all images to Glide's cache
                for (ChordQuestion question : chordQuestions) {
                    String imageUrl = getFullStorageUrl(question.getImageUrl());

                    // Preload image to Glide's cache
                    Glide.with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload();
                }

                // Then preload and cache audio files
                for (ChordQuestion question : chordQuestions) {
                    String audioUrl = question.getAudioUrl();
                    preloadAudioFile(audioUrl);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error preloading media", e);
            } finally {
                isPreloadingMedia = false;
            }
        });
    }

    private void preloadAudioFile(String audioUrl) {
        if (audioUrl == null || audioUrl.isEmpty() || audioCache.containsKey(audioUrl)) {
            return;
        }

        try {
            String fullUrl = getFullStorageUrl(audioUrl);

            // Create a unique file for this audio in the app's cache directory
            File cacheDir = new File(context.getCacheDir(), "audio_cache");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            String fileName = audioUrl.substring(audioUrl.lastIndexOf("/") + 1);
            File audioFile = new File(cacheDir, fileName);

            // Check if already cached
            if (audioFile.exists()) {
                audioCache.put(audioUrl, audioFile);
                return;
            }

            // Download the file
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(audioFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            output.close();
            input.close();

            // Store in cache
            audioCache.put(audioUrl, audioFile);

            // Optionally pre-create MediaPlayer
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(audioFile.getPath());
            player.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            player.prepare(); // Prepare synchronously since we're in a background thread

            mediaPlayerCache.put(audioUrl, player);

        } catch (Exception e) {
            Log.e(TAG, "Error preloading audio file: " + audioUrl, e);
        }
    }

    public void loadImageIntoView(String imageUrl, ImageView imageView) {
        String fullUrl = getFullStorageUrl(imageUrl);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache everything
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert);

        Glide.with(context)
                .load(fullUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    public void prepareAudioPlayer(String audioUrl, final AudioPreparedCallback callback) {
        executorService.execute(() -> {
            try {
                // Check if we have a cached MediaPlayer
                if (mediaPlayerCache.containsKey(audioUrl)) {
                    MediaPlayer player = mediaPlayerCache.get(audioUrl);

                    // Reset player to start position
                    if (player != null) {
                        try {
                            player.seekTo(0);
                            callback.onAudioPrepared(player);
                            return;
                        } catch (IllegalStateException e) {
                            // If player is in an error state, remove it from cache
                            mediaPlayerCache.remove(audioUrl);
                            // Continue to create a new one
                        }
                    }
                }

                // Check if we have the file cached
                if (audioCache.containsKey(audioUrl)) {
                    File audioFile = audioCache.get(audioUrl);

                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(audioFile.getPath());
                    player.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );

                    player.setOnPreparedListener(mp -> {
                        // Store in cache
                        mediaPlayerCache.put(audioUrl, player);
                        callback.onAudioPrepared(player);
                    });

                    player.prepareAsync();
                    return;
                }

                // No cached version, download directly
                String fullUrl = getFullStorageUrl(audioUrl);

                // Start downloading to cache and preparing in the background
                preloadAudioFile(audioUrl);

                // Meanwhile, create a player that streams directly
                MediaPlayer streamingPlayer = new MediaPlayer();
                streamingPlayer.setDataSource(fullUrl);
                streamingPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );

                streamingPlayer.setOnPreparedListener(mp -> {
                    callback.onAudioPrepared(streamingPlayer);
                });

                streamingPlayer.setOnErrorListener((mp, what, extra) -> {
                    callback.onError("MediaPlayer error: " + what);
                    return true;
                });

                streamingPlayer.prepareAsync();

            } catch (Exception e) {
                Log.e(TAG, "Error preparing audio", e);
                callback.onError("Error preparing audio: " + e.getMessage());
            }
        });
    }

    public String getFullStorageUrl(String filePath) {
        // Make sure filePath has no leading slash
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        return SUPABASE_URL + "/storage/v1/object/public/" + STORAGE_BUCKET + "/" + filePath;
    }

    public void cleanup() {
        // Release all cached MediaPlayers
        for (MediaPlayer player : mediaPlayerCache.values()) {
            if (player != null) {
                try {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                    player.release();
                } catch (Exception e) {
                    Log.e(TAG, "Error releasing MediaPlayer", e);
                }
            }
        }
        mediaPlayerCache.clear();

        // Shutdown executor service
        executorService.shutdown();
    }

    public interface DataLoadCallback {
        void onDataLoaded(List<ChordQuestion> questions);
        void onError(String errorMessage);
    }

    public interface ChordLibraryCallback {
        void onDataLoaded(List<ChordItem> chords);
        void onError(String errorMessage);
    }

    public interface AudioPreparedCallback {
        void onAudioPrepared(MediaPlayer mediaPlayer);
        void onError(String errorMessage);
    }
}