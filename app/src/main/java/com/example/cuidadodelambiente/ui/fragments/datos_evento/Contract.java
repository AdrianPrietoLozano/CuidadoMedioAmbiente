package com.example.cuidadodelambiente.ui.fragments.datos_evento;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

import java.util.List;

public interface Contract {
    interface View {
        void showLoading();
        void showEvento(EventoLimpieza eventoLimpieza);
        void showError(String error);
    }

    interface Presenter {
        void fetchEventos();
        void onEventosFetched(List<EventoLimpieza> eventos);
        void onEventosError(String error);
    }

    interface Model {
        void fetchEventos();
    }
}
