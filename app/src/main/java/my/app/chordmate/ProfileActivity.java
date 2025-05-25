package my.app.chordmate;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChordMatePrefs";
    private static final String USER_CHORDS_KEY = "user_chords";
    private static final String REMINDER_ENABLED_KEY = "reminder_enabled";
    private static final String REMINDER_HOUR_KEY = "reminder_hour";
    private static final String REMINDER_MINUTE_KEY = "reminder_minute";
    private static final String NOTIFICATION_CHANNEL_ID = "practice_reminder_channel";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private static final int SCHEDULE_EXACT_ALARM_REQUEST_CODE = 101;
    private static final int REMINDER_REQUEST_CODE = 1001;

    private TextView usernameText;
    private TextView chordsAddedText;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button saveButton;
    private Button logoutButton;
    private SwitchCompat reminderSwitch;
    private TextView reminderTimeText;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userReference;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if user is signed in
        if (currentUser == null) {
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
        initializeViews();

        // Create notification channel
        createNotificationChannel();

        // Request notification permission if needed
        requestNotificationPermission();

        // Request exact alarm permission
        requestExactAlarmPermission();

        // Set email from FirebaseUser as a fallback
        if (currentUser.getEmail() != null) {
            emailInput.setText(currentUser.getEmail());
            String email = currentUser.getEmail();
            String fallbackUsername = email.split("@")[0];
            usernameText.setText(fallbackUsername);
        }

        // Load user data from Firebase
        loadUserData();

        // Load and display chord count
        loadChordCount();

        // Load app settings
        loadAppSettings();

        // Set up button listeners
        setupListeners();

        // Restore reminder if enabled
        restoreReminder();
    }

    private void initializeViews() {
        usernameText = findViewById(R.id.profile_username);
        chordsAddedText = findViewById(R.id.chords_added_value);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        saveButton = findViewById(R.id.save_button);
        logoutButton = findViewById(R.id.logout_button);
        reminderSwitch = findViewById(R.id.remind_switch);
        reminderTimeText = findViewById(R.id.reminder_time_text);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> updateUserProfile());
        logoutButton.setOnClickListener(v -> logoutUser());
        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showTimePickerDialog();
            } else {
                cancelDailyReminder();
                saveReminderSettings(false, 0, 0);
            }
        });
        reminderTimeText.setOnClickListener(v -> {
            if (reminderSwitch.isChecked()) {
                showTimePickerDialog();
            }
        });
    }

    private void restoreReminder() {
        boolean reminderEnabled = prefs.getBoolean(REMINDER_ENABLED_KEY, false);
        if (reminderEnabled) {
            int hour = prefs.getInt(REMINDER_HOUR_KEY, 19);
            int minute = prefs.getInt(REMINDER_MINUTE_KEY, 0);
            scheduleDailyReminder(hour, minute);
        }
    }

    private void loadChordCount() {
        List<Map<String, String>> userChords = loadUserChords();
        int chordCount = userChords.size();
        chordsAddedText.setText(String.valueOf(chordCount));
    }

    private List<Map<String, String>> loadUserChords() {
        String json = prefs.getString(USER_CHORDS_KEY, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    private void loadAppSettings() {
        boolean reminderEnabled = prefs.getBoolean(REMINDER_ENABLED_KEY, false);
        int reminderHour = prefs.getInt(REMINDER_HOUR_KEY, 19);
        int minute = prefs.getInt(REMINDER_MINUTE_KEY, 0);

        reminderSwitch.setChecked(reminderEnabled);
        updateReminderTimeDisplay(reminderHour, minute);
    }

    private void updateReminderTimeDisplay(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String timeString = sdf.format(calendar.getTime());
        reminderTimeText.setText("Reminder time: " + timeString);
    }

    private void showTimePickerDialog() {
        int currentHour = prefs.getInt(REMINDER_HOUR_KEY, 19);
        int currentMinute = prefs.getInt(REMINDER_MINUTE_KEY, 0);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    updateReminderTimeDisplay(hourOfDay, minute);
                    saveReminderSettings(true, hourOfDay, minute);
                    scheduleDailyReminder(hourOfDay, minute);
                    Toast.makeText(this, "Daily reminder set!", Toast.LENGTH_SHORT).show();
                },
                currentHour,
                currentMinute,
                false // 12-hour format
        );

        timePickerDialog.setTitle("Select Reminder Time");
        timePickerDialog.show();
    }

    private void saveReminderSettings(boolean enabled, int hour, int minute) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(REMINDER_ENABLED_KEY, enabled);
        editor.putInt(REMINDER_HOUR_KEY, hour);
        editor.putInt(REMINDER_MINUTE_KEY, minute);
        editor.apply();
    }

    private void scheduleDailyReminder(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If time has passed for today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Cancel any existing alarms
        alarmManager.cancel(pendingIntent);

        // Schedule new alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission();
            return;
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    private void cancelDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Daily reminder cancelled", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Practice Reminders";
            String description = "Daily practice reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(0xFF00FF00);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, SCHEDULE_EXACT_ALARM_REQUEST_CODE);
            }
        }
    }

    private void requestBatteryOptimizationExemption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                restoreReminder();
            } else {
                Toast.makeText(this, "Notification permission is required for reminders", Toast.LENGTH_LONG).show();
                reminderSwitch.setChecked(false);
                saveReminderSettings(false, 0, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCHEDULE_EXACT_ALARM_REQUEST_CODE) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                int hour = prefs.getInt(REMINDER_HOUR_KEY, 19);
                int minute = prefs.getInt(REMINDER_MINUTE_KEY, 0);
                scheduleDailyReminder(hour, minute);
                Toast.makeText(this, "Exact alarm permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Exact alarm permission required for reliable reminders", Toast.LENGTH_LONG).show();
                reminderSwitch.setChecked(false);
                saveReminderSettings(false, 0, 0);
            }
        }
    }

    private void loadUserData() {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        if (dataSnapshot.hasChild("username")) {
                            String username = dataSnapshot.child("username").getValue(String.class);
                            if (username != null && !username.isEmpty()) {
                                usernameText.setText(username);
                            }
                        } else {
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
                Toast.makeText(ProfileActivity.this,
                        "Update Firebase Database Rules in console",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUserProfile() {
        String newEmail = emailInput.getText().toString().trim();
        String newPassword = passwordInput.getText().toString().trim();

        if (!newEmail.equals(currentUser.getEmail()) && !newEmail.isEmpty()) {
            currentUser.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
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