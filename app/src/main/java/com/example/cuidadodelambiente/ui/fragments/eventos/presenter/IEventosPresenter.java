package com.example.cuidadodelambiente.ui.fragments.eventos.presenter;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;

import java.util.List;

public interface IEventosPresenter {
    void cargarEventos();
    void onConexionExitosa(List<UbicacionEvento> eventos);
    void onConexionError(Throwable t);
}
