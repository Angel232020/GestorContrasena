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

    // COMPONENTES
    TextInputEditText txtNombre;
    TextInputEditText txtCorreo;
    TextInputEditText txtTelefono;
    TextInputEditText txtPass;

    MaterialButton btnCrearCuenta;

    TextView txtVolverLogin;

    // FIREBASE
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 🔥 FIREBASE
        mAuth = FirebaseAuth.getInstance();

        // 🔗 VINCULAR COMPONENTES XML
        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtPass = findViewById(R.id.txtPass);

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        txtVolverLogin = findViewById(R.id.txtVolverLogin);

        // 🔘 BOTÓN REGISTRO
        btnCrearCuenta.setOnClickListener(v -> registrarUsuario());

        // 🔙 VOLVER LOGIN
        txtVolverLogin.setOnClickListener(v -> {

            Intent intent =
                    new Intent(register.this,
                            MainActivity.class);

            startActivity(intent);
            finish();
        });
    }

    // 🔥 REGISTRAR USUARIO
    private void registrarUsuario() {

        String nombre =
                txtNombre.getText().toString().trim();

        String correo =
                txtCorreo.getText().toString().trim();

        String telefono =
                txtTelefono.getText().toString().trim();

        String password =
                txtPass.getText().toString().trim();

        // ✅ VALIDAR CAMPOS
        if (nombre.isEmpty()
                || correo.isEmpty()
                || telefono.isEmpty()
                || password.isEmpty()) {

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

        // ✅ VALIDAR TELÉFONO
        if (!telefono.matches("^\\+[0-9]{8,15}$")) {

            Toast.makeText(this,
                    "Número inválido. Ejemplo: +50370000000",
                    Toast.LENGTH_LONG).show();

            return;
        }

        // ✅ VALIDAR PASSWORD
        if (password.length() < 6) {

            Toast.makeText(this,
                    "La contraseña debe tener mínimo 6 caracteres",
                    Toast.LENGTH_LONG).show();

            return;
        }

        // 🔒 DESHABILITAR BOTÓN
        btnCrearCuenta.setEnabled(false);
        btnCrearCuenta.setText("Creando...");

        // 🔥 CREAR USUARIO FIREBASE
        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {

                    // ❌ ERROR
                    if (!task.isSuccessful()) {

                        btnCrearCuenta.setEnabled(true);
                        btnCrearCuenta.setText("Crear cuenta");

                        Toast.makeText(this,
                                "Error: " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();

                        return;
                    }

                    // ✅ USUARIO CREADO
                    FirebaseUser user =
                            mAuth.getCurrentUser();

                    if (user == null) {

                        Toast.makeText(this,
                                "Error inesperado",
                                Toast.LENGTH_LONG).show();

                        return;
                    }

                    String uid = user.getUid();

                    // 📦 DATOS FIRESTORE
                    Map<String, Object> data =
                            new HashMap<>();

                    data.put("uid", uid);
                    data.put("nombre", nombre);
                    data.put("email", correo);
                    data.put("telefono", telefono);

                    // 🔥 GUARDAR EN FIRESTORE
                    FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(uid)
                            .set(data)
                            .addOnSuccessListener(unused -> {

                                // 📧 ENVIAR VERIFICACIÓN
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {

                                            btnCrearCuenta.setEnabled(true);
                                            btnCrearCuenta.setText("Crear cuenta");

                                            if (emailTask.isSuccessful()) {

                                                Toast.makeText(this,
                                                        "Cuenta creada.\nVerifica tu correo.",
                                                        Toast.LENGTH_LONG).show();

                                                // 🔒 CERRAR SESIÓN
                                                mAuth.signOut();

                                                // 🔙 LOGIN
                                                Intent intent =
                                                        new Intent(register.this,
                                                                MainActivity.class);

                                                startActivity(intent);
                                                finish();

                                            } else {

                                                Toast.makeText(this,
                                                        "No se pudo enviar el correo",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> {

                                btnCrearCuenta.setEnabled(true);
                                btnCrearCuenta.setText("Crear cuenta");

                                Toast.makeText(this,
                                        "Error Firestore: " +
                                                e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                });
    }
}