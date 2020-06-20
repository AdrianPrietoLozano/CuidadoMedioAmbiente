package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

public class UbicacionReporte {
    @SerializedName("id_reporte")
    private Integer id;
    @SerializedName("latitud")
    private Double latitud;
    @SerializedName("longitud")
    private Double longitud;


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

