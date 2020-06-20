package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;



public class ReporteContaminacion {

    @SerializedName("id")
    private Integer id;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora")
    private String hora;

    @SerializedName("tipo_residuo")
    private String tipoResiduo;

    @SerializedName("volumen")
    private String volumenResiduo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("latitud")
    private Double latitud;

    @SerializedName("longitud")
    private Double longitud;

    @SerializedName("creador")
    private String ambientalista;

    @SerializedName("foto")
    private String rutaFoto;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(String tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public String getVolumenResiduo() {
        return volumenResiduo;
    }

    public void setVolumenResiduo(String volumenResiduo) {
        this.volumenResiduo = volumenResiduo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getAmbientalista() {
        return ambientalista;
    }

    public void setAmbientalista(String ambientalista) {
        this.ambientalista = ambientalista;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }
}
