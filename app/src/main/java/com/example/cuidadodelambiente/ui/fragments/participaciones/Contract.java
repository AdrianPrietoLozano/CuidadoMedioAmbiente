package com.example.cuidadodelambiente.ui.fragments.participaciones;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.ui.base.MvpPresenter;
import com.example.cuidadodelambiente.ui.base.MvpView;

import java.util.List;
import java.util.Observer;

public interface Contract {
    interface View extends MvpView {
        void showEventos(List<EventoLimpieza> eventos);
        void showError(String error);
        void hideContenido();
        void showNoEventos();
    }

    interface Presenter<V extends View> extends MvpPresenter<V> {
        void fetchEventos();
        void onEventosFetched(List<EventoLimpieza> eventos);
        void onEventosError(String error);
    }

    interface Model {
        void fetchEventos();
    }
}
