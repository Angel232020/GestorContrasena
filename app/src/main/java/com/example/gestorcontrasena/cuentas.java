package com.example.gestorcontrasena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class cuentas extends AppCompatActivity {

    RecyclerView recyclerView;
    MaterialButton btnAgregar;

    ArrayList<CuentaModel> lista;
    CuentaAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

        recyclerView = findViewById(R.id.recyclerCuentas);
        btnAgregar = findViewById(R.id.btnAgregarCuenta);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        lista = new ArrayList<>();

        adapter = new CuentaAdapter(this, lista);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, Agregar_Cuenta.class))
        );

        cargarDatos();
    }

    private void cargarDatos() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this,
                    "No hay sesión iniciada",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("cuentas")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        CuentaModel cuenta = doc.toObject(CuentaModel.class);

                        if (cuenta != null) {

                            // 🔥 CLAVE PARA EDITAR Y ELIMINAR
                            cuenta.setId(doc.getId());

                            lista.add(cuenta);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error Firestore: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos(); // 🔥 refresca cuando vuelves de otra pantalla
    }
}