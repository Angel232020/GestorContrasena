package com.example.gestorcontrasena;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // 🔗 COMPONENTES
    TextInputEditText txtUsuario;
    TextInputEditText txtPassword;

    MaterialButton btnLogin;

    TextView txtRegistrar;

    // 🔥 FIREBASE
    FirebaseAuth mAuth;

    // 🔐 OTP
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔥 FIREBASE
        mAuth = FirebaseAuth.getInstance();

        // 🔗 XML
        txtUsuario = findViewById(R.id.txtUsuario);

        txtPassword = findViewById(R.id.txtPassword);

        btnLogin = findViewById(R.id.btnLogin);

        txtRegistrar = findViewById(R.id.txtRegistrar);

        // 🔐 LOGIN
        btnLogin.setOnClickListener(v -> loginUsuario());

        // 📝 IR A REGISTRO
        txtRegistrar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MainActivity.this,
                            register.class);

            startActivity(intent);
        });
    }

    // 🔄 REACTIVAR BOTÓN
    private void resetLoginButton() {

        btnLogin.setEnabled(true);
        btnLogin.setText("Ingresar");
    }

    // 🔥 LOGIN
    private void loginUsuario() {

        String correo =
                txtUsuario.getText().toString().trim();

        String password =
                txtPassword.getText().toString().trim();

        // ✅ VALIDAR CAMPOS
        if (correo.isEmpty() || password.isEmpty()) {

            Toast.makeText(this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // ✅ VALIDAR EMAIL
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {

            Toast.makeText(this,
                    "Correo inválido",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // 🔒 DESHABILITAR BOTÓN
        btnLogin.setEnabled(false);
        btnLogin.setText("Ingresando...");

        // 🔥 LOGIN FIREBASE
        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {

                    // ❌ LOGIN INCORRECTO
                    if (!task.isSuccessful()) {

                        resetLoginButton();

                        Toast.makeText(this,
                                "Correo o contraseña incorrectos",
                                Toast.LENGTH_LONG).show();

                        return;
                    }

                    // 🔥 USUARIO ACTUAL
                    FirebaseUser user =
                            mAuth.getCurrentUser();

                    // ❌ ERROR USUARIO
                    if (user == null) {

                        resetLoginButton();

                        Toast.makeText(this,
                                "Error de autenticación",
                                Toast.LENGTH_LONG).show();

                        return;
                    }

                    // 🔄 RECARGAR USUARIO
                    user.reload().addOnCompleteListener(reloadTask -> {

                        // ❌ EMAIL NO VERIFICADO
                        if (!user.isEmailVerified()) {

                            mAuth.signOut();

                            resetLoginButton();

                            Toast.makeText(this,
                                    "Debes verificar tu correo electrónico",
                                    Toast.LENGTH_LONG).show();

                            return;
                        }

                        // 🔥 BUSCAR TELÉFONO EN FIRESTORE
                        FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(user.getUid())
                                .get()
                                .addOnSuccessListener(doc -> {

                                    // ❌ NO EXISTE
                                    if (!doc.exists()) {

                                        resetLoginButton();

                                        Toast.makeText(this,
                                                "Usuario no encontrado",
                                                Toast.LENGTH_LONG).show();

                                        return;
                                    }

                                    // 📱 TELÉFONO
                                    String telefono =
                                            doc.getString("telefono");

                                    // ❌ TELÉFONO VACÍO
                                    if (telefono == null
                                            || telefono.isEmpty()) {

                                        resetLoginButton();

                                        Toast.makeText(this,
                                                "Número no registrado",
                                                Toast.LENGTH_LONG).show();

                                        return;
                                    }

                                    // 📲 ENVIAR OTP
                                    enviarOTP(telefono);
                                })
                                .addOnFailureListener(e -> {

                                    resetLoginButton();

                                    Toast.makeText(this,
                                            "Error Firestore: "
                                                    + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    });
                });
    }

    // 📲 ENVIAR OTP
    private void enviarOTP(String telefono) {

        btnLogin.setText("Enviando OTP...");

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
                public void onCodeSent(
                        String verificationId,
                        PhoneAuthProvider.ForceResendingToken token) {

                    MainActivity.this.verificationId =
                            verificationId;

                    resetLoginButton();

                    Toast.makeText(MainActivity.this,
                            "Código enviado",
                            Toast.LENGTH_SHORT).show();

                    // 👉 IR A OTP
                    Intent intent =
                            new Intent(MainActivity.this,
                                    Otp.class);

                    intent.putExtra(
                            "verificationId",
                            verificationId
                    );

                    startActivity(intent);
                }

                @Override
                public void onVerificationCompleted(
                        PhoneAuthCredential credential) {

                    // ✅ AUTO VERIFICACIÓN OPCIONAL
                }

                @Override
                public void onVerificationFailed(
                        FirebaseException e) {

                    resetLoginButton();

                    // 🔥 MOSTRAR ERROR REAL
                    e.printStackTrace();

                    Toast.makeText(
                            MainActivity.this,
                            "Error OTP: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            };
}