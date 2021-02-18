package com.example.cuidadodelambiente.ui.fragments.datos_evento.presenter;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

public interface IDatosEventoPresenter {
    public void cargarDatosEvento(int idEvento);
    public void participarEnEvento(int idEvento);
    public void dejarDeParticiparEnEvento(int idEvento);
    public void cancelarCargarDatosEvento();

    public void onCargarDatosEventoError(String error);
    public void onParticiparEnEventoError(int resultado, String error);
    public void onDejarParticiparEventoError(int resultado, String error);

    public void onCargarDatosEventoExito(EventoLimpieza evento);
    public void onParticiparEnEventoExito();
    public void onDejarParticiparEventoExito();

}
