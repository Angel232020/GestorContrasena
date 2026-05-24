package com.example.gestorcontrasena;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CuentaAdapter extends RecyclerView.Adapter<CuentaAdapter.ViewHolder> {

    Context context;
    ArrayList<CuentaModel> lista;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CuentaAdapter(Context context, ArrayList<CuentaModel> lista) {
        this.context = context; // 🔥 IMPORTANTE: NO usar getApplicationContext()
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cuenta, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CuentaModel cuenta = lista.get(position);

        holder.txtNombre.setText(cuenta.getNombre());
        holder.txtCorreo.setText(cuenta.getCorreo());
        holder.txtApp.setText("App: " + cuenta.getApp());

        // 🔥 NUEVO: contraseña oculta por defecto
        holder.txtPassword.setText("••••••••");

        // 👁️ mostrar/ocultar contraseña
        holder.txtPassword.setOnClickListener(v -> {

            if (holder.txtPassword.getText().toString().contains("•")) {
                holder.txtPassword.setText(cuenta.getPassword());
            } else {
                holder.txtPassword.setText("••••••••");
            }
        });

        holder.btnEditar.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            CuentaModel cuentaEdit = lista.get(pos);

            Intent intent = new Intent(v.getContext(), EditarCuenta.class);

            intent.putExtra("id", cuentaEdit.getId());
            intent.putExtra("nombre", cuentaEdit.getNombre());
            intent.putExtra("correo", cuentaEdit.getCorreo());
            intent.putExtra("app", cuentaEdit.getApp());
            intent.putExtra("password", cuentaEdit.getPassword());

            v.getContext().startActivity(intent);
        });

        holder.btnEliminar.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Eliminar")
                    .setMessage("¿Seguro que deseas eliminar esta cuenta?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        db.collection("cuentas")
                                .document(cuenta.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    lista.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, lista.size());

                                    Toast.makeText(v.getContext(),
                                            "Eliminado correctamente",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(v.getContext(),
                                                "Error: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtCorreo, txtApp, txtPassword;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCorreo = itemView.findViewById(R.id.txtCorreo);
            txtApp = itemView.findViewById(R.id.txtApp);

            // 🔥 NUEVO TEXTVIEW
            txtPassword = itemView.findViewById(R.id.txtPassword);

            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}