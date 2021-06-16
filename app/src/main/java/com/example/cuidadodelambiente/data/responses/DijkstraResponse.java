package com.example.cuidadodelambiente.data.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DijkstraResponse {
    @SerializedName("ruta")
    private List<DijkstraResponse> ruta;

    @SerializedName("estatus")
    private StatusResponse estatus;

    public DijkstraResponse(List<DijkstraResponse> ruta, StatusResponse estatus) {
        this.ruta = ruta;
        this.estatus = estatus;
    }

    public List<DijkstraResponse> getRuta() {
        return ruta;
    }

    public void setRuta(List<DijkstraResponse> ruta) {
        this.ruta = ruta;
    }

    public StatusResponse getEstatus() {
        return estatus;
    }

    public void setEstatus(StatusResponse estatus) {
        this.estatus = estatus;
    }
}
