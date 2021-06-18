package com.example.cuidadodelambiente.data.responses;

import com.example.cuidadodelambiente.data.models.UbicacionDijkstra;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DijkstraResponse {
    @SerializedName("ruta")
    private List<UbicacionDijkstra> ruta;

    @SerializedName("estatus")
    private StatusResponse estatus;

    public DijkstraResponse(List<UbicacionDijkstra> ruta, StatusResponse estatus) {
        this.ruta = ruta;
        this.estatus = estatus;
    }

    public List<UbicacionDijkstra> getRuta() {
        return ruta;
    }

    public void setRuta(List<UbicacionDijkstra> ruta) {
        this.ruta = ruta;
    }

    public StatusResponse getEstatus() {
        return estatus;
    }

    public void setEstatus(StatusResponse estatus) {
        this.estatus = estatus;
    }
}
