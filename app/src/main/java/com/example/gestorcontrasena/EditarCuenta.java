package com.example.gestorcontrasena;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarCuenta extends AppCompatActivity {

    TextInputEditText txtNombreCuenta, txtCorreo, txtPassword;
    Spinner spinnerApps;

    MaterialButton btnActualizarCuenta, btnVolver;

    FirebaseFirestore db;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cuenta);

        // 🔥 IDs CORRECTOS (según tu XML)
        txtNombreCuenta = findViewById(R.id.txtNombreCuenta);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);

        spinnerApps = findViewById(R.id.spinnerApps);

        btnActualizarCuenta = findViewById(R.id.btnActualizarCuenta);
        btnVolver = findViewById(R.id.btnVolver);

        db = FirebaseFirestore.getInstance();

        // 🔥 Spinner opciones
        String[] apps = {"Facebook", "Instagram", "Google", "Twitter", "Otro"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                apps
        );

        spinnerApps.setAdapter(adapter);

        // 🔥 recibir datos
        id = getIntent().getStringExtra("id");

        txtNombreCuenta.setText(getIntent().getStringExtra("nombre"));
        txtCorreo.setText(getIntent().getStringExtra("correo"));
        txtPassword.setText(getIntent().getStringExtra("password"));

        String app = getIntent().getStringExtra("app");

        if (app != null) {
            for (int i = 0; i < apps.length; i++) {
                if (apps[i].equals(app)) {
                    spinnerApps.setSelection(i);
                    break;
                }
            }
        }

        // 🔥 actualizar
        btnActualizarCuenta.setOnClickListener(v -> actualizar());

        // 🔙 volver
        btnVolver.setOnClickListener(v -> finish());
    }

    private void actualizar() {

        db.collection("cuentas")
                .document(id)
                .update(
                        "nombre", txtNombreCuenta.getText().toString(),
                        "correo", txtCorreo.getText().toString(),
                        "password", txtPassword.getText().toString(),
                        "app", spinnerApps.getSelectedItem().toString()
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cuenta actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}