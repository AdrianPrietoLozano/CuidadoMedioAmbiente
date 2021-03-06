package com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

import java.util.List;

public interface IRecomendacionesEventosPresenter {
    void cargarRecomendacionesEventos();
    void onConexionExitosa(List<EventoLimpieza> eventos);
    void onConexionError(Throwable t);
}
