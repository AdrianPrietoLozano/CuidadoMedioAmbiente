package com.example.cuidadodelambiente.ui.fragments.participaciones.view;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

import java.util.List;

public interface IParticipacionesEventosView {
    void onEventosCargadosExitosamente(List<EventoLimpieza> eventos);
    void onEventosCargadosError(Throwable t);
}
