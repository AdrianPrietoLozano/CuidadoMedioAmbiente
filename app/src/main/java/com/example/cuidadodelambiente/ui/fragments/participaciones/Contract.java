package com.example.cuidadodelambiente.ui.fragments.participaciones;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;

import java.util.List;
import java.util.Observer;

public interface Contract {
    interface View {
        void showLoading();
        void hideLoading();
        void showEventos(List<EventoLimpieza> eventos);
        void showError(String error);
        void showNoEventos();
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
