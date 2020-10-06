package com.example.cuidadodelambiente.data.responses;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.google.gson.annotations.SerializedName;

public class EventoLimpiezaResponse {

    @SerializedName("evento")
    private EventoLimpieza evento;

    @SerializedName("estatus")
    private StatusResponse status;

    public EventoLimpieza getEvento() {
        return evento;
    }

    public void setEvento(EventoLimpieza evento) {
        this.evento = evento;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }
}
