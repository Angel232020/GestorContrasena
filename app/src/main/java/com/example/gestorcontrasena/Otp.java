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

    TextInputEditText txtOtp;
    MaterialButton btnVerificar;

    FirebaseAuth mAuth;

    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();

        txtOtp = findViewById(R.id.txtOtp);
        btnVerificar = findViewById(R.id.btnVerificar);

        // 🔐 recibir verificationId desde MainActivity
        verificationId = getIntent().getStringExtra("verificationId");

        btnVerificar.setOnClickListener(v -> verificarCodigo());
    }

    // 🔥 VERIFICAR OTP
    private void verificarCodigo() {

        String code = txtOtp.getText().toString().trim();

        // ⚠️ VALIDACIONES
        if (code.isEmpty()) {
            Toast.makeText(this,
                    "Ingresa el código OTP",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (verificationId == null || verificationId.isEmpty()) {
            Toast.makeText(this,
                    "Error de verificación",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔐 CREAR CREDENCIAL OTP
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(verificationId, code);

        // 🔐 VALIDAR OTP
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Acceso autorizado",
                                Toast.LENGTH_SHORT).show();

                        // 👉 IR AL DASHBOARD
                        Intent intent = new Intent(Otp.this, Dashboard.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(this,
                                "Código incorrecto o expirado",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}