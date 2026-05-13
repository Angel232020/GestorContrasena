package com.example.gestorcontrasena;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Dashboard extends AppCompatActivity {

    MaterialButton btnCerrarSesion;

    MaterialCardView btnCuentas, btnTarjetas, btnPasswords, btnDocumentos;

    TextView txtSaludo;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔥 TEXTVIEW SALUDO
        txtSaludo = findViewById(R.id.txtSaludo);

        cargarNombre();

        // BOTÓN LOGOUT
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        // MENÚ
        btnCuentas = findViewById(R.id.btnCuentas);
        btnTarjetas = findViewById(R.id.btnTarjetas);
        btnPasswords = findViewById(R.id.btnPasswords);
        btnDocumentos = findViewById(R.id.btnDocumentos);

        btnCuentas.setOnClickListener(v ->
                startActivity(new Intent(this, cuentas.class)));

        btnTarjetas.setOnClickListener(v ->
                startActivity(new Intent(this, tarjetas.class)));

        btnPasswords.setOnClickListener(v ->
                startActivity(new Intent(this, Contrasenas.class)));

        btnDocumentos.setOnClickListener(v ->
                startActivity(new Intent(this, Documentos.class)));

        // EDGE TO EDGE
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(android.R.id.content),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    // 🔥 CARGAR NOMBRE DESDE FIRESTORE
    private void cargarNombre() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            txtSaludo.setText("Hola Usuario");
            return;
        }

        String uid = user.getUid();

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        String nombre = document.getString("nombre");

                        // 🔥 DEBUG IMPORTANTE
                        if (nombre == null || nombre.trim().isEmpty()) {
                            txtSaludo.setText("Hola Usuario");
                        } else {
                            txtSaludo.setText("Hola " + nombre);
                        }

                    } else {
                        txtSaludo.setText("Hola Usuario");
                    }

                })
                .addOnFailureListener(e -> {
                    txtSaludo.setText("Hola Usuario");
                });
        Log.d("UID_DEBUG", "UID: " + uid);
        Toast.makeText(this, uid, Toast.LENGTH_LONG).show();
    }

    // 🔥 LOGOUT
    private void cerrarSesion() {

        mAuth.signOut();

        Toast.makeText(this,
                "Sesión cerrada",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}