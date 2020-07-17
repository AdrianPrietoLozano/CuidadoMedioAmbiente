package com.example.cuidadodelambiente.ui.fragments.participaciones.presenter;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

import java.util.List;

public interface IParticipacionesEventosPresenter {
    void cargarParticipacionesEventos(Integer idUsuario);
    void onConexionExitosa(List<EventoLimpieza> eventos);
    void onConexionError(Throwable t);
}
