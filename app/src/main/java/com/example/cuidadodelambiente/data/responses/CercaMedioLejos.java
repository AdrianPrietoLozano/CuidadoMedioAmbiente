package com.example.cuidadodelambiente.data.responses;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CercaMedioLejos {
    @SerializedName("cerca")
    private List<UbicacionEvento> cerca;

    @SerializedName("medio")
    private List<UbicacionEvento> medio;

    @SerializedName("lejos")
    private List<UbicacionEvento> lejos;

    public List<UbicacionEvento> getCerca() {
        return cerca;
    }

    public void setCerca(List<UbicacionEvento> cerca) {
        this.cerca = cerca;
    }

    public List<UbicacionEvento> getMedio() {
        return medio;
    }

    public void setMedio(List<UbicacionEvento> medio) {
        this.medio = medio;
    }

    public List<UbicacionEvento> getLejos() {
        return lejos;
    }

    public void setLejos(List<UbicacionEvento> lejos) {
        this.lejos = lejos;
    }
}
