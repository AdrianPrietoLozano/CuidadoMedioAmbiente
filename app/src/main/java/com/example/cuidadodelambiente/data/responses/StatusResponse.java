package com.example.cuidadodelambiente.data.responses;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
    @SerializedName("resultado")
    private Integer resultado;

    @SerializedName("mensaje")
    private String mensaje;

    public StatusResponse(Integer resultado, String mensaje) {
        this.resultado = resultado;
        this.mensaje = mensaje;
    }

    public Integer getResultado() {
        return resultado;
    }

    public void setResultado(Integer resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
