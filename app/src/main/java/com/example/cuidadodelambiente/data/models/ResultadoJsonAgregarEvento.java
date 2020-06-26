package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

public class ResultadoJsonAgregarEvento {
    @SerializedName("resultado")
    private Integer resultado;
    @SerializedName("mensaje")
    private String mensaje;
    @SerializedName("id_evento")
    private Integer idEvento;

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

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer id_evento) {
        this.idEvento = id_evento;
    }
}
