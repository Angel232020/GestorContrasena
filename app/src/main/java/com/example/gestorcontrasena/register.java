package com.example.gestorcontrasena;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    TextInputEditText txtNombre, txtCorreo, txtTelefono, txtPass;
    MaterialButton btnCrearCuenta;
    TextView txtVolverLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtPass = findViewById(R.id.txtPass);

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        txtVolverLogin = findViewById(R.id.txtVolverLogin);

        btnCrearCuenta.setOnClickListener(v -> registrarUsuario());

        //BOTÓN VOLVER AL LOGIN
        txtVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(register.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registrarUsuario() {

        String nombre = txtNombre.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String password = txtPass.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!telefono.startsWith("+")) {
            Toast.makeText(this, "Teléfono debe iniciar con +503", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCrearCuenta.setEnabled(false);
        btnCrearCuenta.setText("Creando...");

        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        btnCrearCuenta.setEnabled(true);
                        btnCrearCuenta.setText("Crear cuenta");

                        Toast.makeText(this,
                                "ERROR: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null) return;

                    String uid = user.getUid();

                    Map<String, Object> data = new HashMap<>();
                    data.put("nombre", nombre);
                    data.put("email", correo);
                    data.put("telefono", telefono);

                    FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(uid)
                            .set(data)
                            .addOnSuccessListener(aVoid -> {

                                Toast.makeText(this,
                                        "Usuario creado",
                                        Toast.LENGTH_SHORT).show();

                                //IR AL LOGIN
                                Intent intent = new Intent(register.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {

                                btnCrearCuenta.setEnabled(true);
                                btnCrearCuenta.setText("Crear cuenta");

                                Toast.makeText(this,
                                        "Error Firestore: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                });
    }
}