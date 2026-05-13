package com.example.gestorcontrasena;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.security.SecureRandom;

public class Contrasenas extends AppCompatActivity {

    TextInputEditText txtLongitud;
    MaterialButton btnGenerar, btnCopiar;
    android.widget.TextView txtPassword;

    String passwordGenerada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasenas);

        txtLongitud = findViewById(R.id.txtLongitud);
        btnGenerar = findViewById(R.id.btnGenerar);
        btnCopiar = findViewById(R.id.btnCopiar);
        txtPassword = findViewById(R.id.txtPassword);

        btnGenerar.setOnClickListener(v -> generarPassword());

        btnCopiar.setOnClickListener(v -> copiar());
    }

    private void generarPassword() {

        String lenStr = txtLongitud.getText().toString().trim();

        if (lenStr.isEmpty()) {
            Toast.makeText(this, "Ingresa la longitud", Toast.LENGTH_SHORT).show();
            return;
        }

        int length = Integer.parseInt(lenStr);

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        passwordGenerada = sb.toString();
        txtPassword.setText(passwordGenerada);
    }

    private void copiar() {

        if (passwordGenerada.isEmpty()) {
            Toast.makeText(this, "Genera una contraseña primero", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("password", passwordGenerada);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copiada al portapapeles", Toast.LENGTH_SHORT).show();
    }
}