package com.example.gestorcontrasena;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Agregar_Cuenta extends AppCompatActivity {

    TextInputEditText txtNombreCuenta;
    TextInputEditText txtCorreo;
    TextInputEditText txtPassword;

    Spinner spinnerApps;
    MaterialButton btnGuardar;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cuenta);

        // XML
        txtNombreCuenta = findViewById(R.id.txtNombreCuenta);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);
        spinnerApps = findViewById(R.id.spinnerApps);
        btnGuardar = findViewById(R.id.btnGuardarCuenta);

        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // SPINNER
        String[] apps = {
                "Ninguna",
                "Netflix",
                "Facebook",
                "Instagram",
                "Google",
                "Spotify"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        apps
                );

        spinnerApps.setAdapter(adapter);

        // BOTÓN GUARDAR
        btnGuardar.setOnClickListener(v -> guardarCuenta());
    }

    private void guardarCuenta() {

        String nombre = txtNombreCuenta.getText() != null ?
                txtNombreCuenta.getText().toString().trim() : "";

        String correo = txtCorreo.getText() != null ?
                txtCorreo.getText().toString().trim() : "";

        String password = txtPassword.getText() != null ?
                txtPassword.getText().toString().trim() : "";

        String appSeleccionada = spinnerApps.getSelectedItem().toString();

        // VALIDACIONES
        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Completa los campos obligatorios",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (appSeleccionada.equals("Ninguna")) {
            appSeleccionada = "Sin app";
        }

        // USUARIO
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this,
                    "Usuario no autenticado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // DATA FIRESTORE
        Map<String, Object> cuenta = new HashMap<>();
        cuenta.put("nombre", nombre);
        cuenta.put("correo", correo);
        cuenta.put("password", password);
        cuenta.put("app", appSeleccionada);
        cuenta.put("uid", user.getUid());

        // GUARDAR
        db.collection("cuentas")
                .add(cuenta)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this,
                            "Cuenta guardada correctamente",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}