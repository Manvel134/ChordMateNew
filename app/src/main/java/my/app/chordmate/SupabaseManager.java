package my.app.chordmate;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SupabaseManager {
    private static final String TAG = "SupabaseManager";

    // Replace with your Supabase project URL and API Key
    private static final String SUPABASE_URL = "https://ppzvdasaudvwfsrhmeew.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBwenZkYXNhdWR2d2ZzcmhtZWV3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDY5NTA5NjAsImV4cCI6MjA2MjUyNjk2MH0.G093MoP4MHxFY0a01FgLqZ2wWR8W1f2YfUh3f898skA";

    // Storage bucket name where you'll store media files
    private static final String STORAGE_BUCKET = "chordmateapp";

    private static SupabaseManager instance;
    private final OkHttpClient httpClient;
    private final Context context;

    private List<ChordQuestion> chordQuestions = new ArrayList<>();
    private boolean isDataLoaded = false;

    private SupabaseManager(Context context) {
        this.context = context.getApplicationContext();
        this.httpClient = new OkHttpClient();
    }

    public static synchronized SupabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new SupabaseManager(context);
        }
        return instance;
    }

    public void loadChordData(final DataLoadCallback callback) {
        if (isDataLoaded && !chordQuestions.isEmpty()) {
            callback.onDataLoaded(chordQuestions);
            return;
        }

        // Fetch chord data from Supabase database
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/chord_questions?select=*")
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to load chord data", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Server error: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonData);

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

                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error", e);
                    callback.onError("Data parsing error: " + e.getMessage());
                }
            }
        });
    }

    public void loadImageIntoView(String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(getFullStorageUrl(imageUrl))
                .into(imageView);
    }

    public void prepareAudioPlayer(String audioUrl, final AudioPreparedCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(getFullStorageUrl(audioUrl));
                mediaPlayer.prepare();
                callback.onAudioPrepared(mediaPlayer);
            } catch (IOException e) {
                Log.e(TAG, "Error preparing audio", e);
                callback.onError("Error loading audio: " + e.getMessage());
            }
        });
    }

    private String getFullStorageUrl(String filePath) {
        return SUPABASE_URL + "/storage/v1/object/public/" + STORAGE_BUCKET + "/" + filePath;
    }

    public interface DataLoadCallback {
        void onDataLoaded(List<ChordQuestion> questions);
        void onError(String errorMessage);
    }

    public interface AudioPreparedCallback {
        void onAudioPrepared(MediaPlayer mediaPlayer);
        void onError(String errorMessage);
    }
}