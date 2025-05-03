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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chordmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupEmail, signupUsername, signupPassword, signupConfirmPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    boolean passwordVisible = false;
    boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById(R.id.signup_confirm_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Set up password toggle functionality
        signupPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the touch is in the drawable right area (visibility icon)
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (signupPassword.getRight() - signupPassword.getCompoundDrawables()[2].getBounds().width())) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });

        // Set up confirm password toggle functionality
        signupConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the touch is in the drawable right area (visibility icon)
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (signupConfirmPassword.getRight() - signupConfirmPassword.getCompoundDrawables()[2].getBounds().width())) {
                        toggleConfirmPasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInputs()) {
                    return;
                }

                String password = signupPassword.getText().toString();
                String confirmPassword = signupConfirmPassword.getText().toString();

                if (!password.equals(confirmPassword)) {
                    signupConfirmPassword.setError("Passwords do not match");
                    return;
                }

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();

                // Create user with email and password
                createUserWithEmailAndPassword(email, password, name, username);
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createUserWithEmailAndPassword(String email, String password, String name, String username) {
        // Show progress indicator or disable button to prevent multiple clicks
        signupButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Send verification email
                            sendEmailVerification();

                            // Save additional user data to Realtime Database
                            saveUserDataToDatabase(user.getUid(), name, email, username);

                            // Show success message
                            Toast.makeText(SignupActivity.this,
                                    "Registration successful! Please check your email for verification.",
                                    Toast.LENGTH_LONG).show();

                            // Redirect to verification pending screen or login
                            redirectToVerificationScreen();
                        } else {
                            // If sign up fails, display a message to the user
                            Toast.makeText(SignupActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            // Re-enable the signup button
                            signupButton.setEnabled(true);
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email sent successfully
                                Toast.makeText(SignupActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Email failed to send
                                Toast.makeText(SignupActivity.this,
                                        "Failed to send verification email: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveUserDataToDatabase(String userId, String name, String email, String username) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        // Create user object
        HelperClass helperClass = new HelperClass(name, email, username, ""); // Don't store password in database

        // Add with userId from Authentication as the key
        reference.child(userId).setValue(helperClass);
    }

    private void redirectToVerificationScreen() {
        // You can create a dedicated verification pending screen
        // or redirect to login with a message
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.putExtra("VERIFICATION_PENDING", true);
        startActivity(intent);
        finish(); // Close signup activity
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Hide password
            signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            signupPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.ic_visibility_off, 0);
        } else {
            // Show password
            signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            signupPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.ic_visibility, 0);
        }
        passwordVisible = !passwordVisible;

        // Move cursor to the end of the text
        signupPassword.setSelection(signupPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (confirmPasswordVisible) {
            // Hide confirm password
            signupConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            signupConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.ic_visibility_off, 0);
        } else {
            // Show confirm password
            signupConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            signupConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.ic_visibility, 0);
        }
        confirmPasswordVisible = !confirmPasswordVisible;

        // Move cursor to the end of the text
        signupConfirmPassword.setSelection(signupConfirmPassword.getText().length());
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (signupName.getText().toString().isEmpty()) {
            signupName.setError("Name cannot be empty");
            isValid = false;
        }

        if (signupEmail.getText().toString().isEmpty()) {
            signupEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!isValidEmail(signupEmail.getText().toString())) {
            signupEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        if (signupUsername.getText().toString().isEmpty()) {
            signupUsername.setError("Username cannot be empty");
            isValid = false;
        }

        if (signupPassword.getText().toString().isEmpty()) {
            signupPassword.setError("Password cannot be empty");
            isValid = false;
        } else if (signupPassword.getText().toString().length() < 6) {
            signupPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (signupConfirmPassword.getText().toString().isEmpty()) {
            signupConfirmPassword.setError("Confirm password cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}