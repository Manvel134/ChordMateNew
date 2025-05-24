package my.app.chordmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.chordmate.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MyChordsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChordMatePrefs";
    private static final String USER_CHORDS_KEY = "user_chords";
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> audioPickerLauncher;
    private Uri selectedImageUri;
    private Uri selectedAudioUri;
    private EditText chordNameInput;
    private List<Map<String, String>> userChords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chords);

        // Request permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_AUDIO
            }, 100);
        }

        View emptyStateContainer = findViewById(R.id.empty_state_container);
        GridLayout chordsGrid = findViewById(R.id.chords_grid);
        MaterialButton addChordButton = findViewById(R.id.add_chord_button);
        MaterialButton backButton = findViewById(R.id.back_to_menu_button);

        // Initialize file pickers
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                    }
                });

        audioPickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedAudioUri = uri;
                        Toast.makeText(this, "Audio selected", Toast.LENGTH_SHORT).show();
                    }
                });

        // Load user chords from SharedPreferences
        userChords = loadUserChords();
        updateChordGrid(chordsGrid, emptyStateContainer);

        addChordButton.setOnClickListener(v -> showAddChordDialog());

        backButton.setOnClickListener(v -> finish());
    }

    private void showAddChordDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_chord, null);
        builder.setView(dialogView);

        chordNameInput = dialogView.findViewById(R.id.chord_name_input);
        MaterialButton selectImageButton = dialogView.findViewById(R.id.select_image_button);
        MaterialButton selectAudioButton = dialogView.findViewById(R.id.select_audio_button);
        MaterialButton saveButton = dialogView.findViewById(R.id.save_chord_button);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancel_button);

        selectImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        selectAudioButton.setOnClickListener(v -> audioPickerLauncher.launch("audio/*"));

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String chordName = chordNameInput.getText().toString().trim();
            if (chordName.isEmpty() || selectedImageUri == null || selectedAudioUri == null) {
                Toast.makeText(this, "Please provide chord name, image, and audio", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Copy image and audio to internal storage
                String imageFileName = "chord_image_" + UUID.randomUUID() + ".jpg";
                String audioFileName = "chord_audio_" + UUID.randomUUID() + ".mp3";

                File imageFile = new File(getFilesDir(), imageFileName);
                File audioFile = new File(getFilesDir(), audioFileName);

                // Copy image
                try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                     FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                // Copy audio
                try (InputStream inputStream = getContentResolver().openInputStream(selectedAudioUri);
                     FileOutputStream outputStream = new FileOutputStream(audioFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                // Save chord data with internal file paths
                Map<String, String> chordData = new HashMap<>();
                chordData.put("name", chordName);
                chordData.put("imagePath", imageFile.getAbsolutePath());
                chordData.put("audioPath", audioFile.getAbsolutePath());
                userChords.add(chordData);
                saveUserChords(userChords);

                // Update UI
                updateChordGrid(findViewById(R.id.chords_grid), findViewById(R.id.empty_state_container));
                dialog.dismiss();
                Toast.makeText(this, "Chord added successfully", Toast.LENGTH_SHORT).show();

                // Reset selections
                selectedImageUri = null;
                selectedAudioUri = null;
            } catch (Exception e) {
                Toast.makeText(this, "Error saving chord: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateChordGrid(GridLayout chordsGrid, View emptyStateContainer) {
        chordsGrid.removeAllViews();
        if (userChords.isEmpty()) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            chordsGrid.setVisibility(View.GONE);
        } else {
            emptyStateContainer.setVisibility(View.GONE);
            chordsGrid.setVisibility(View.VISIBLE);

            for (Map<String, String> chord : userChords) {
                View chordCard = LayoutInflater.from(this).inflate(R.layout.chord_card, null);
                ImageView chordImage = chordCard.findViewById(R.id.chord_image);
                TextView chordNameText = chordCard.findViewById(R.id.chord_name);

                chordNameText.setText(chord.get("name"));
                String imagePath = chord.get("imagePath");
                if (imagePath != null) {
                    Uri imageUri = Uri.fromFile(new File(imagePath));
                    chordImage.setImageURI(imageUri);
                }

                chordCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MyChordsActivity.this, FullscreenChordActivity.class);
                    intent.putExtra("chord_image_path", chord.get("imagePath"));
                    intent.putExtra("chord_audio_path", chord.get("audioPath"));
                    intent.putExtra("chord_name", chord.get("name"));
                    intent.putExtra("chord_description", "User-added chord");
                    startActivity(intent);
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(8, 8, 8, 8);
                chordCard.setLayoutParams(params);
                chordsGrid.addView(chordCard);
            }
        }
    }

    private List<Map<String, String>> loadUserChords() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(USER_CHORDS_KEY, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    private void saveUserChords(List<Map<String, String>> chords) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chords);
        editor.putString(USER_CHORDS_KEY, json);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "Permissions required to add chords", Toast.LENGTH_SHORT).show();
            }
        }
    }
}