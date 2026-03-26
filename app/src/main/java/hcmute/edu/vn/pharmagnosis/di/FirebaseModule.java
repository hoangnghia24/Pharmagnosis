package hcmute.edu.vn.pharmagnosis.di;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseModule {
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}