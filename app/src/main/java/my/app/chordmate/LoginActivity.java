package my.app.chordmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText, forgotPasswordText;
    private FirebaseAuth mAuth;
    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        forgotPasswordText = findViewById(R.id.forgotPassword);

        // Check if coming from signup with verification pending
        if (getIntent().getBooleanExtra("VERIFICATION_PENDING", false)) {
            Toast.makeText(this, "Please verify your email before logging in", Toast.LENGTH_LONG).show();
        }

        // Password visibility toggle
        loginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the touch is in the drawable right area (visibility icon)
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (loginPassword.getRight() - loginPassword.getCompoundDrawables()[2].getBounds().width())) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                }

                String email = loginUsername.getText().toString();
                String password = loginPassword.getText().toString();

                // Disable button to prevent multiple attempts
                loginButton.setEnabled(false);

                // Sign in with email and password
                signInWithEmailAndPassword(email, password);
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Forgot password functionality
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginUsername.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                    loginUsername.setError("Email required for password reset");
                    return;
                }

                sendPasswordResetEmail(email);
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, check if email is verified
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user.isEmailVerified()) {
                                // Email is verified, proceed to main app
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Email is not verified
                                Toast.makeText(LoginActivity.this,
                                        "Please verify your email before logging in",
                                        Toast.LENGTH_LONG).show();

                                // Send verification email again
                                user.sendEmailVerification();

                                // Sign out the user
                                mAuth.signOut();
                            }
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // Re-enable login button
                        loginButton.setEnabled(true);
                    }
                });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Password reset email sent to " + email,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send reset email: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Hide password
            loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.ic_visibility_off, 0);
        } else {
            // Show password
            loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.ic_visibility, 0);
        }
        passwordVisible = !passwordVisible;

        // Move cursor to the end of the text
        loginPassword.setSelection(loginPassword.getText().length());
    }

    private Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Email cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }
}