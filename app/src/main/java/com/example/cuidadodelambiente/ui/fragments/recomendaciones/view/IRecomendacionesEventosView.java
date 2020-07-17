package com.example.cuidadodelambiente.ui.fragments.recomendaciones.view;

import com.example.cuidadodelambiente.data.models.EventoItem;
import java.util.List;

public interface IRecomendacionesEventosView {
    void onEventosCargadosExitosamente(List<EventoItem> eventos);
    void onEventosCargadosError(Throwable t);
}
