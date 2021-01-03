package com.example.cuidadodelambiente.data.models;

import com.example.cuidadodelambiente.data.responses.StatusResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventoLimpieza {

    @SerializedName("id_evento")
    private Integer idEvento;

    @SerializedName("id_reporte")
    private Integer idReporte;

    private LatLng ubicacion;

    @SerializedName("creador")
    private User creador;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora")
    private String hora;

    @SerializedName("descripcion")
    private String descripcion;

    // "0" si no ha sido administrado
    // "1" si ha sido administrado
    @SerializedName("administrado")
    private String administrado;


    @SerializedName("foto")
    private String rutaFotografia;

    @SerializedName("personas_unidas")
    private int numPersonasUnidas;

    @SerializedName("residuos")
    private List<String> residuos;

    @SerializedName("usuario_participa")
    private boolean usuarioParticipa;


    public List<String> getResiduos() {
        return residuos;
    }

    public void setResiduos(List<String> residuos) {
        this.residuos = residuos;
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

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public User getCreador() {
        return creador;
    }

    public void setCreador(User creador) {
        this.creador = creador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Integer idReporte) {
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

    public String getAdministrado() {
        return administrado;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean getUsuarioParticipa() {
        return usuarioParticipa;
    }

}
