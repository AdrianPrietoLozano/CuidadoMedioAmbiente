package com.example.cuidadodelambiente.ui.fragments.datos_evento;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.ui.BasePresenter;

import java.util.List;

public interface Contract {
    interface View {
        void showLoading();
        void hideLoading();
        void showLoadingDialog();
        void hideLoadingDialog();
        void showError(String error);
        void hideError();
        void showEvento(EventoLimpieza evento);
        void showMessage(String msg);
        void showRecomendaciones(List<EventoLimpieza> eventos);
        void enableDejarParticipar();
        void enableParticipacion();
        void enableAdministrarEvento();
        void onParticiparEventoExito();
        void onDejarParticiparEventoExito();
        void hideAllButtons();

    }

    interface Presenter extends BasePresenter {
        void fetchEvento(int idEvento);
        void participarEnEvento(int idEvento);
        void dejarDeParticiparEnEvento(int idEvento);

        void onEventoFetched(EventoLimpieza eventoLimpieza);
        void onParticiparEnEventoExito();
        void onDejarParticiparEventoExito();

        void onFetchEventoError(String error);
        void onParticiparEnEventoError(String error);
        void onDejarParticiparEventoError(String error);

        void fetchRecomendacionesEvento(int idEvento);
        void onRecomnedacionesFetched(List<EventoLimpieza> eventos);
        void onRecomendacionesError(String error);
    }

    interface Model {
        void fetchEvento(int idEvento);
        void participarEnEvento(int idEvento);
        void dejarDeParticiparEnEvento(int idEvento);
        void fetchRecomendacionesEvento(int idEvento);
    }
}
