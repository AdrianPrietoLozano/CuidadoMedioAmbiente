package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

public class UbicacionEvento {

    @SerializedName("id_evento")
    private Integer id;
    @SerializedName("latitud")
    private Double latitud;
    @SerializedName("longitud")
    private Double longitud;

    public UbicacionEvento(int id, Double latitud, Double longitud) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
