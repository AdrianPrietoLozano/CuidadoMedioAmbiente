package com.example.cuidadodelambiente.ui.fragments.recomendaciones.view;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

import java.util.List;

public interface IRecomendacionesEventosView {
    void onEventosCargadosExitosamente(List<EventoLimpieza> eventos);
    void onEventosCargadosError(Throwable t);
}
