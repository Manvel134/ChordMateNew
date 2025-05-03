package my.app.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private TextView emailTextView;
    private Button resendButton;
    private Button checkVerificationButton;
    private Button loginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // No user is signed in, redirect to login
            redirectToLogin();
            return;
        }

        // Initialize UI components
        emailTextView = findViewById(R.id.verification_email);
        resendButton = findViewById(R.id.resend_verification_button);
        checkVerificationButton = findViewById(R.id.check_verification_button);
        loginButton = findViewById(R.id.goto_login_button);

        // Set email text
        emailTextView.setText(user.getEmail());

        // Set up buttons
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationEmail();
            }
        });

        checkVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailVerification();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToLogin();
            }
        });
    }

    private void resendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Disable button temporarily
            resendButton.setEnabled(false);

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(VerificationActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(VerificationActivity.this,
                                        "Failed to send verification email: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                            // Re-enable button
                            resendButton.setEnabled(true);
                        }
                    });
        }
    }

    private void checkEmailVerification() {
        // Get fresh instance of current user
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        Toast.makeText(VerificationActivity.this,
                                "Email verified! You can now login.",
                                Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    } else {
                        Toast.makeText(VerificationActivity.this,
                                "Email not verified yet. Please check your inbox.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already verified
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            redirectToLogin();
        }
    }
}