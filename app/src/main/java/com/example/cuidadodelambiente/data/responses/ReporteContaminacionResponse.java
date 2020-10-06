package com.example.cuidadodelambiente.data.responses;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.google.gson.annotations.SerializedName;

public class ReporteContaminacionResponse {

    @SerializedName("reporte")
    private ReporteContaminacion reporte;

    @SerializedName("estatus")
    private StatusResponse status;

    public ReporteContaminacion getReporte() {
        return reporte;
    }

    public void setReporte(ReporteContaminacion reporte) {
        this.reporte = reporte;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }
}
