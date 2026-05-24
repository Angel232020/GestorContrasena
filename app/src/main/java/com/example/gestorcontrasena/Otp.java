package com.example.gestorcontrasena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class Otp extends AppCompatActivity {

    // COMPONENTES
    TextInputEditText txtOtp;
    MaterialButton btnVerificar;

    // FIREBASE
    FirebaseAuth mAuth;

    // OTP
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // 🔥 FIREBASE
        mAuth = FirebaseAuth.getInstance();

        // 🔗 XML
        txtOtp = findViewById(R.id.txtOtp);
        btnVerificar = findViewById(R.id.btnVerificar);

        // 🔐 RECIBIR verificationId
        verificationId =
                getIntent().getStringExtra("verificationId");

        // 🔘 BOTÓN
        btnVerificar.setOnClickListener(v -> verificarCodigo());
    }

    // 🔥 VERIFICAR OTP
    private void verificarCodigo() {

        String code =
                txtOtp.getText().toString().trim();

        // ✅ VALIDAR OTP
        if (code.isEmpty()) {

            Toast.makeText(this,
                    "Ingresa el código OTP",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // ✅ VALIDAR verificationId
        if (verificationId == null
                || verificationId.isEmpty()) {

            Toast.makeText(this,
                    "Error de verificación",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // 🔒 DESHABILITAR BOTÓN
        btnVerificar.setEnabled(false);
        btnVerificar.setText("Verificando...");

        try {

            // 🔐 CREAR CREDENTIAL
            PhoneAuthCredential credential =
                    PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );

            // 🔥 VALIDAR OTP CON FIREBASE
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {

                        // ❌ ERROR
                        if (!task.isSuccessful()) {

                            btnVerificar.setEnabled(true);
                            btnVerificar.setText("Verificar");

                            Toast.makeText(this,
                                    "Código incorrecto",
                                    Toast.LENGTH_LONG).show();

                            return;
                        }

                        // ✅ OTP CORRECTO
                        Toast.makeText(this,
                                "OTP verificado",
                                Toast.LENGTH_SHORT).show();

                        // 👉 DASHBOARD
                        Intent intent =
                                new Intent(Otp.this,
                                        Dashboard.class);

                        startActivity(intent);
                        finish();
                    });

        } catch (Exception e) {

            btnVerificar.setEnabled(true);
            btnVerificar.setText("Verificar");

            Toast.makeText(this,
                    "Código inválido o expirado",
                    Toast.LENGTH_LONG).show();
        }
    }
}