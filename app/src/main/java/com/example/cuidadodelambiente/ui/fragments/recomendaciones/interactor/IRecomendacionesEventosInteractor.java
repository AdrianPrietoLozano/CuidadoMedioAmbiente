package com.example.cuidadodelambiente.ui.fragments.recomendaciones.interactor;

import com.example.cuidadodelambiente.data.models.EventoItem;

import java.util.List;

public interface IRecomendacionesEventosInteractor {
    void onConexionExitosa(List<EventoItem> eventos);
    void onConexionError();
}
