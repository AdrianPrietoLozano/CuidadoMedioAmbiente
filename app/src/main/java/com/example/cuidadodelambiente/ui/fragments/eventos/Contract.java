package com.example.cuidadodelambiente.ui.fragments.eventos;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.ui.base.MvpPresenter;
import com.example.cuidadodelambiente.ui.base.MvpView;

import java.util.List;

public interface Contract {
    interface View extends MvpView {
        void showEventos(List<UbicacionEvento> eventos);
        void showError(String error);
        void hideContenido();
    }

    interface Presenter<V extends View> extends MvpPresenter<V> {
        void fetchEventos();
        void onEventosFetched(List<UbicacionEvento> eventos);
        void onEventosError(String error);
    }

    interface Model {
        void fetchEventos();
    }
}
