package com.example.cuidadodelambiente.data.responses;

import com.google.gson.annotations.SerializedName;

public class CrearLimpiezaResponse {

    @SerializedName("estatus")
    private StatusResponse estatus;

    @SerializedName("id_limpieza")
    private Integer idLimpieza;

    public CrearLimpiezaResponse(StatusResponse estatus, Integer idLimpieza) {
        this.estatus = estatus;
        this.idLimpieza = idLimpieza;
    }

    public StatusResponse getEstatus() {
        return estatus;
    }

    public void setEstatus(StatusResponse estatus) {
        this.estatus = estatus;
    }

    public Integer getIdLimpieza() {
        return idLimpieza;
    }

    public void setIdLimpieza(Integer idLimpieza) {
        this.idLimpieza = idLimpieza;
    }
}
