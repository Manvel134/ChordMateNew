package my.app.chordmate;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Helper class to manage Firebase Authentication and Database operations
 */
public class FirebaseAuthManager {
    private static final String TAG = "FirebaseAuthManager";
    private static final String DB_USERS_PATH = "users";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Context mContext;

    public FirebaseAuthManager(Context context) {
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();

        // Enable offline persistence for better performance and offline capabilities
        mDatabase.setPersistenceEnabled(true);
    }

    /**
     * Create a new user and save their data in the Realtime Database
     */
    public void createUser(String email, String password, final String name, final String username,
                           final OnUserOperationListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Send verification email
                            sendEmailVerification();

                            // Save user data to database
                            if (user != null) {
                                saveUserData(user.getUid(), name, email, username, listener);
                            } else {
                                if (listener != null) {
                                    listener.onFailure("Failed to get user after registration");
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onFailure(task.getException() != null ?
                                        task.getException().getMessage() : "Registration failed");
                            }
                        }
                    }
                });
    }

    /**
     * Save user data to Firebase Realtime Database
     */
    private void saveUserData(String userId, String name, String email, String username,
                              final OnUserOperationListener listener) {
        DatabaseReference userRef = mDatabase.getReference(DB_USERS_PATH).child(userId);

        // Create user object
        HelperClass helperClass = new HelperClass(name, email, username, "");

        // Log the database operation for debugging
        Log.d(TAG, "Saving user data for ID: " + userId);
        Log.d(TAG, "Database reference: " + userRef.toString());

        // Save user data with userId from Authentication as the key
        userRef.setValue(helperClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User data saved successfully");
                            if (listener != null) {
                                listener.onSuccess("User registered successfully");
                            }
                        } else {
                            Log.e(TAG, "Failed to save user data: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                            if (listener != null) {
                                listener.onFailure("Failed to save user data");
                            }
                        }
                    }
                });
    }

    /**
     * Send email verification to current user
     */
    public void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Verification email sent to " + user.getEmail());
                            } else {
                                Log.e(TAG, "Failed to send verification email", task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * Get current user data from Firebase Realtime Database
     */
    public void getCurrentUserData(final OnUserDataListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (listener != null) {
                listener.onFailure("User not logged in");
            }
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = mDatabase.getReference(DB_USERS_PATH).child(userId);

        // Debug info
        Log.d(TAG, "Getting user data for ID: " + userId);
        Log.d(TAG, "Using database reference: " + userRef.toString());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange called, exists: " + dataSnapshot.exists());
                if (dataSnapshot.exists()) {
                    try {
                        HelperClass userData = dataSnapshot.getValue(HelperClass.class);
                        if (listener != null && userData != null) {
                            listener.onDataReceived(userData);
                        } else {
                            listener.onFailure("Failed to parse user data");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing user data", e);
                        if (listener != null) {
                            listener.onFailure("Error parsing user data: " + e.getMessage());
                        }
                    }
                } else {
                    Log.w(TAG, "No user data found in database for ID: " + userId);
                    if (listener != null) {
                        listener.onFailure("No user data found");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "User data read cancelled", databaseError.toException());
                if (listener != null) {
                    listener.onFailure("Database error: " + databaseError.getMessage());
                }
            }
        });
    }

    /**
     * Sign in a user with email and password
     */
    public void signIn(String email, String password, final OnUserOperationListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    if (listener != null) {
                                        listener.onSuccess("Login successful");
                                    }
                                } else {
                                    // Email is not verified
                                    if (listener != null) {
                                        listener.onEmailVerificationNeeded();
                                    }
                                    // Sign out the user
                                    mAuth.signOut();
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onFailure(task.getException() != null ?
                                        task.getException().getMessage() : "Authentication failed");
                            }
                        }
                    }
                });
    }

    /**
     * Sign out the current user
     */
    public void signOut() {
        mAuth.signOut();
    }

    /**
     * Check if current user exists and is logged in
     */
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Get current Firebase user
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Listener interface for user operations
     */
    public interface OnUserOperationListener {
        void onSuccess(String message);
        void onFailure(String errorMessage);
        void onEmailVerificationNeeded();
    }

    /**
     * Listener interface for receiving user data
     */
    public interface OnUserDataListener {
        void onDataReceived(HelperClass userData);
        void onFailure(String errorMessage);
    }
}