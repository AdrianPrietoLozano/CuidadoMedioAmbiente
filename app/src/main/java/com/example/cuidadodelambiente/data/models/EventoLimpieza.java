package com.example.cuidadodelambiente.data.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class EventoLimpieza {

    private int idEvento;

    private int idReporte;

    private LatLng ubicacion;

    @SerializedName("creador")
    private String ambientalista;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora")
    private String hora;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("foto")
    private String rutaFotografia;

    @SerializedName("personas_unidas")
    private int numPersonasUnidas;

    @SerializedName("residuo")
    private String tipoResiduo;

    public String getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(String tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public int getNumPersonasUnidas() {
        return numPersonasUnidas;
    }

    public void setNumPersonasUnidas(int numPersonasUnidas) {
        this.numPersonasUnidas = numPersonasUnidas;
    }

    public String getRutaFotografia() {
        return rutaFotografia;
    }

    public void setRutaFotografia(String rutaFotografia) {
        this.rutaFotografia = rutaFotografia;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getAmbientalista() {
        return ambientalista;
    }

    public void setAmbientalista(String ambientalista) {
        this.ambientalista = ambientalista;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }
}
