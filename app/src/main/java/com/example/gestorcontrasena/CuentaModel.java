package com.example.gestorcontrasena;

public class CuentaModel {

    String nombre;
    String correo;
    String app;
    String password;
    private String id;
    public CuentaModel() {} // 🔥 IMPORTANTE para Firestore

    public CuentaModel(String nombre, String correo, String app) {
        this.nombre = nombre;
        this.correo = correo;
        this.app = app;
        this.password = password;
    }

    public String getNombre() {
        return nombre != null ? nombre : "";
    }

    public String getCorreo() {
        return correo != null ? correo : "";
    }

    public String getApp() {
        return app != null ? app : "";
    }
    public String getPassword() {
        return password != null ? password : "";
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}