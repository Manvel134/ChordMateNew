package my.app.chordmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chordmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChordMatePrefs";
    private static final String USER_CHORDS_KEY = "user_chords";

    private TextView usernameText;
    private TextView chordsAddedText;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button saveButton;
    private Button logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if user is signed in
        if (currentUser == null) {
            // Not signed in, redirect to login activity
            redirectToLogin();
            return;
        }

        // Initialize Firebase Database reference
        userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        // Initialize UI components
        usernameText = findViewById(R.id.profile_username);
        chordsAddedText = findViewById(R.id.chords_added_value);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        saveButton = findViewById(R.id.save_button);
        logoutButton = findViewById(R.id.logout_button);

        // Set email from FirebaseUser as a fallback
        if (currentUser.getEmail() != null) {
            emailInput.setText(currentUser.getEmail());

            // If we can't access database, at least show the authenticated user's info
            // Email username part (everything before @) as a fallback username
            String email = currentUser.getEmail();
            String fallbackUsername = email.split("@")[0];
            usernameText.setText(fallbackUsername);
        }

        // Load user data from Firebase
        loadUserData();

        // Load and display chord count
        loadChordCount();

        // Set up save button listener
        saveButton.setOnClickListener(v -> {
            updateUserProfile();
        });

        // Set up logout button listener
        logoutButton.setOnClickListener(v -> {
            logoutUser();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh chord count when returning to this activity
        loadChordCount();
    }

    private void loadChordCount() {
        List<Map<String, String>> userChords = loadUserChords();
        int chordCount = userChords.size();
        chordsAddedText.setText(String.valueOf(chordCount));
    }

    private List<Map<String, String>> loadUserChords() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(USER_CHORDS_KEY, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    private void loadUserData() {
        // Get additional user data from Realtime Database
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    try {
                        // Try to get username directly first
                        if (dataSnapshot.hasChild("username")) {
                            String username = dataSnapshot.child("username").getValue(String.class);
                            if (username != null && !username.isEmpty()) {
                                usernameText.setText(username);
                            }
                        }
                        // Then try using HelperClass
                        else {
                            HelperClass userData = dataSnapshot.getValue(HelperClass.class);
                            if (userData != null && userData.getUsername() != null) {
                                usernameText.setText(userData.getUsername());
                            }
                        }

                    } catch (Exception e) {
                        Toast.makeText(ProfileActivity.this,
                                "Error parsing data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "No user data found in database",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,
                        "Permission denied: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();

                // Show a more helpful message about Firebase rules
                Toast.makeText(ProfileActivity.this,
                        "Update Firebase Database Rules in console",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUserProfile() {
        String newEmail = emailInput.getText().toString().trim();
        String newPassword = passwordInput.getText().toString().trim();

        // Update email if changed
        if (!newEmail.equals(currentUser.getEmail()) && !newEmail.isEmpty()) {
            currentUser.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update email in Realtime Database as well
                                userReference.child("email").setValue(newEmail);
                                Toast.makeText(ProfileActivity.this,
                                        "Email updated successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this,
                                        "Failed to update email: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        // Update password if provided
        if (!newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                passwordInput.setError("Password must be at least 6 characters");
                return;
            }

            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this,
                                        "Password updated successfully",
                                        Toast.LENGTH_SHORT).show();
                                passwordInput.setText("");
                            } else {
                                Toast.makeText(ProfileActivity.this,
                                        "Failed to update password: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}