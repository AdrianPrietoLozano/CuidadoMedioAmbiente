package com.example.cuidadodelambiente.ui.fragments.datos_evento.view;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

public interface IDatosEventoView {

    public void onCargarDatosEventoError(String error);
    public void onParticiparEnEventoError(int resultado, String error);
    public void onDejarParticiparEventoError(int resultado, String error);

    public void onCargarDatosEventoExito(EventoLimpieza evento);
    public void onParticiparEnEventoExito();
    public void onDejarParticiparEventoExito();

}
