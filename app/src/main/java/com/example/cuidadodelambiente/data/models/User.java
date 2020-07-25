package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

public class User {

    public static final int USUARIO_GOOGLE = 1;
    public static final int USUARIO_NORMAL = 2;

    @SerializedName("id")
    private Integer id;

    @SerializedName("email")
    private String email;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("puntos")
    private Integer puntos;

    @SerializedName("resultado")
    private Integer resultado;

    @SerializedName("mensaje")
    private String mensaje;

    private int tipoUsuario;

    public User(Integer id, String nombre, String email, Integer puntos, int tipo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.puntos = puntos;
        this.tipoUsuario = tipo;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipo) {
        this.tipoUsuario = tipo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public Integer getResultado() {
        return resultado;
    }

    public void setResultado(Integer resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
