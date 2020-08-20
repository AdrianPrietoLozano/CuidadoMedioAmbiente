package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

public class ParticipaEventoItem {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("fecha")
    private String  fecha;

    @SerializedName("hora")
    private String hora;

    @SerializedName("creador")
    private String creador;

    @SerializedName("tipo")
    private String tipoResiduo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("foto")
    private String foto;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public String getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(String tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
