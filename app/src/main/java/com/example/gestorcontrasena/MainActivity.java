package com.example.gestorcontrasena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    TextInputEditText txtUsuario, txtPassword;
    MaterialButton btnLogin;
    TextView txtRegistrar;

    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // 🔐 LOGIN CAMPOS
        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 🧾 REGISTRO LINK
        txtRegistrar = findViewById(R.id.txtRegistrar);

        // 👉 IR A REGISTRO (ARREGLADO)
        txtRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, register.class);
            startActivity(intent);
        });

        // 🔐 LOGIN
        btnLogin.setOnClickListener(v -> loginUser());
    }

    // 🔐 LOGIN + OTP FLOW
    private void loginUser() {

        String email = txtUsuario.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 1. LOGIN FIREBASE AUTH
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                "Credenciales incorrectas",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    // ☁️ 2. OBTENER TELEFONO DESDE FIRESTORE
                    FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(doc -> {

                                if (!doc.exists()) {
                                    Toast.makeText(this,
                                            "Usuario no registrado en Firestore",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }

                                String telefono = doc.getString("telefono");

                                if (telefono == null || telefono.isEmpty()) {
                                    Toast.makeText(this,
                                            "Teléfono no registrado",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // 📲 3. ENVIAR OTP
                                enviarOTP(telefono);
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Error Firestore: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show()
                            );
                });
    }

    // 📲 ENVIAR OTP
    private void enviarOTP(String telefono) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(telefono)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // 🔥 CALLBACKS OTP
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {

                    MainActivity.this.verificationId = verificationId;

                    // 👉 IR A OTP
                    Intent intent = new Intent(MainActivity.this, Otp.class);
                    intent.putExtra("verificationId", verificationId);

                    startActivity(intent);
                    finish();
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // Auto-login opcional
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {

                    Toast.makeText(MainActivity.this,
                            "Error OTP: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            };
}