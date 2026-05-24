package com.example.gestorcontrasena;

import android.content.Intent;
import android.os.Bundle;
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

    // 🔗 COMPONENTES
    MaterialButton btnCerrarSesion;

    MaterialCardView btnCuentas;
    MaterialCardView btnTarjetas;
    MaterialCardView btnPasswords;
    MaterialCardView btnDocumentos;

    TextView txtSaludo;

    // 🔥 FIREBASE
    FirebaseAuth mAuth;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_dashboard);

        // 🔥 FIREBASE
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        // 🔗 XML
        txtSaludo = findViewById(R.id.txtSaludo);

        btnCerrarSesion =
                findViewById(R.id.btnCerrarSesion);

        btnCuentas =
                findViewById(R.id.btnCuentas);

        btnTarjetas =
                findViewById(R.id.btnTarjetas);

        btnPasswords =
                findViewById(R.id.btnPasswords);

        btnDocumentos =
                findViewById(R.id.btnDocumentos);

        // 🔥 CARGAR NOMBRE
        cargarNombre();

        // 🔒 LOGOUT
        btnCerrarSesion.setOnClickListener(v ->
                cerrarSesion());

        // 📁 CUENTAS
        btnCuentas.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Dashboard.this,
                            cuentas.class);

            startActivity(intent);
        });

        // 💳 TARJETAS
        btnTarjetas.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Dashboard.this,
                            tarjetas.class);

            startActivity(intent);
        });

        // 🔑 PASSWORDS
        btnPasswords.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Dashboard.this,
                            Contrasenas.class);

            startActivity(intent);
        });

        // 📄 DOCUMENTOS
        btnDocumentos.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Dashboard.this,
                            Documentos.class);

            startActivity(intent);
        });

        // 🔥 EDGE TO EDGE
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(android.R.id.content),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
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

        FirebaseUser user =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        // ❌ SIN SESIÓN
        if (user == null) {

            txtSaludo.setText("Hola Usuario");

            return;
        }

        // 🔥 UID
        String uid = user.getUid();

        // 🔥 FIRESTORE
        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    // ✅ EXISTE
                    if (document.exists()) {

                        String nombre =
                                document.getString("nombre");

                        // ✅ VALIDAR NOMBRE
                        if (nombre != null
                                && !nombre.trim().isEmpty()) {

                            txtSaludo.setText(
                                    "Hola " + nombre
                            );

                        } else {

                            txtSaludo.setText(
                                    "Hola Usuario"
                            );
                        }

                    } else {

                        txtSaludo.setText(
                                "Hola Usuario"
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    txtSaludo.setText(
                            "Hola Usuario"
                    );
                });
    }

    // 🔒 CERRAR SESIÓN
    private void cerrarSesion() {

        // 🔥 LOGOUT FIREBASE
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(this,
                "Sesión cerrada",
                Toast.LENGTH_SHORT).show();

        // 🔄 LIMPIAR STACK
        Intent intent =
                new Intent(Dashboard.this,
                        MainActivity.class);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // 🔙 BLOQUEAR VOLVER
    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }
}