package com.example.cuidadodelambiente.Entidades;

import android.graphics.Bitmap;
import android.text.format.Time;

import com.google.android.gms.maps.model.LatLng;

import java.security.Timestamp;
import java.util.Date;


public class ReporteContaminacion {
    int id;
    String fecha;
    String hora;
    String tipoResiduo;
    String volumenResiduo;
    String descripcion;
    LatLng ubicacion;
    String ambientalista;
    String rutaFotografia;
    private Bitmap fotografia;

    public String getRutaFotografia() {
        return rutaFotografia;
    }

    public void setRutaFotografia(String rutaFotografia) {
        this.rutaFotografia = rutaFotografia;
    }

    public Bitmap getFotografia() {
        return fotografia;
    }

    public void setFotografia(Bitmap fotografia) {
        this.fotografia = fotografia;
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

    public String getAmbientalista() {
        return ambientalista;
    }

    public void setAmbientalista(String ambientalista) {
        this.ambientalista = ambientalista;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }
}
