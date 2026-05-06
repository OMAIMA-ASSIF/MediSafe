package com.example.medisafe.ui.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();
    private final FirebaseAuth firebaseAuth;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<AuthState> getAuthState() { return authState; }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            authState.setValue(AuthState.error("Remplis tous les champs"));
            return;
        }
        authState.setValue(AuthState.loading());

        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(result -> authState.setValue(AuthState.success(false)))
                .addOnFailureListener(e -> {
                    String msg = "Erreur de connexion";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("password")) msg = "Mot de passe incorrect";
                        else if (e.getMessage().contains("user")) msg = "Compte introuvable";
                        else if (e.getMessage().contains("network")) msg = "Vérifie ta connexion internet";
                    }
                    authState.setValue(AuthState.error(msg));
                });
    }

    public void register(String email, String password, String name) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            authState.setValue(AuthState.error("Remplis tous les champs"));
            return;
        }
        if (password.length() < 6) {
            authState.setValue(AuthState.error("Le mot de passe doit avoir au moins 6 caractères"));
            return;
        }
        authState.setValue(AuthState.loading());

        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(result -> {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    result.getUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> authState.setValue(AuthState.success(true)));
                })
                .addOnFailureListener(e -> authState.setValue(AuthState.error("Erreur: " + e.getLocalizedMessage())));
    }

    public void resetPassword(String email) {
        if (email.isEmpty()) {
            authState.setValue(AuthState.error("Entre ton email d'abord"));
            return;
        }
        firebaseAuth.sendPasswordResetEmail(email.trim())
                .addOnSuccessListener(v -> authState.setValue(AuthState.error("Email envoyé ! Vérifie ta boîte mail ✅")))
                .addOnFailureListener(e -> authState.setValue(AuthState.error("Email introuvable")));
    }

    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public static class AuthState {
        public enum Status { LOADING, SUCCESS, ERROR }
        public final Status status;
        public final String message;
        public final boolean isNewUser;

        private AuthState(Status status, String message, boolean isNewUser) {
            this.status = status;
            this.message = message;
            this.isNewUser = isNewUser;
        }
        public static AuthState loading() { return new AuthState(Status.LOADING, null, false); }
        public static AuthState success(boolean isNewUser) { return new AuthState(Status.SUCCESS, null, isNewUser); }
        public static AuthState error(String msg) { return new AuthState(Status.ERROR, msg, false); }
    }
}