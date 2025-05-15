package my.app.chordmate;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chordmate.R;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    // These would typically come from your user authentication system
    private String username = "GuitarLover123";
    private String email = "guitarist@example.com";
    private int chordsAdded = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        // Initialize UI components
        TextView usernameText = findViewById(R.id.profile_username);
        TextView chordsAddedText = findViewById(R.id.chords_added_value);

        TextInputEditText emailInput = findViewById(R.id.email_input);
        TextInputEditText passwordInput = findViewById(R.id.password_input);

        Button saveButton = findViewById(R.id.save_button);
        Button logoutButton = findViewById(R.id.logout_button);

        // Populate UI with user data
        usernameText.setText(username);
        chordsAddedText.setText(String.valueOf(chordsAdded));
        emailInput.setText(email);

        // Set up button listeners
        saveButton.setOnClickListener(v -> {
            // Save profile changes
            email = emailInput.getText().toString();
            String newPassword = passwordInput.getText().toString();

            // Here you would typically update the user's information in your database

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            // Handle logout - clear user session, go to login screen, etc.
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
        });
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