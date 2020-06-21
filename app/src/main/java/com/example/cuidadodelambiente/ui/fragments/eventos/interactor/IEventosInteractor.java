package com.example.cuidadodelambiente.ui.fragments.eventos.interactor;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;

import java.util.List;

public interface IEventosInteractor {
    void onConexionExitosa(List<UbicacionEvento> eventos);
    void onConexionError();
}