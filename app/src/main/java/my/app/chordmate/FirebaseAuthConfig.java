package my.app.chordmate;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Application class to initialize Firebase components
 * Add this class to your AndroidManifest.xml:
 *
 * <application
 *     android:name="my.app.chordmate.FirebaseAuthConfig"
 *     ...
 * </application>
 */
public class FirebaseAuthConfig extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase Auth
        FirebaseAuth.getInstance();
    }
}