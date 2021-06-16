package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

public class UbicacionDijkstra {
    @SerializedName("id")
    private Integer id;

    @SerializedName("puntos")
    private Integer puntos;

    @SerializedName("latitud")
    private Double latitud;

    @SerializedName("longitud")
    private Double longitud;

    public UbicacionDijkstra(Integer id, Integer puntos, Double latitud, Double longitud) {
        this.id = id;
        this.puntos = puntos;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
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
}
