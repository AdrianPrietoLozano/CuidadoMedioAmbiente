package com.example.cuidadodelambiente.data.responses;

import com.google.gson.annotations.SerializedName;

public class CrearEventoResponse {
    @SerializedName("id_evento")
    private Integer idEvento;

    @SerializedName("estatus")
    private StatusResponse status;

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }
}
