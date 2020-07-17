package com.example.cuidadodelambiente.ui.fragments.eventos.view;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;

import java.util.List;

public interface IEventosView {
    void onEventosCargadosExitosamente(List<UbicacionEvento> eventos);
    void onEventosCargadosError(Throwable t);
}
