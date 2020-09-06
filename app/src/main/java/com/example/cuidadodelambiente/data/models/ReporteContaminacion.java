package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ReporteContaminacion {

    @SerializedName("id_reporte")
    private Integer id;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora")
    private String hora;

    @SerializedName("residuos")
    private List<String> residuos;

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

    @SerializedName("tiene_evento")
    private boolean tieneEvento;

    @SerializedName("tiene_limpieza")
    private boolean tieneLimpieza;

    // nada que ver con un reporte
    @SerializedName("resultado")
    private int resultado;

    // nada que ver con un reporte
    @SerializedName("mensaje")
    private String mensaje;


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

    public String getVolumenResiduo() {
        return volumenResiduo;
    }

    public void setVolumenResiduo(String volumenResiduo) {
        this.volumenResiduo = volumenResiduo;
    }

    public List<String> getResiduos() {
        return residuos;
    }

    public void setResiduos(List<String> residuos) {
        this.residuos = residuos;
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

    public boolean getTieneEvento() {
        return tieneEvento;
    }

    public boolean getTieneLimpieza() {
        return tieneLimpieza;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
