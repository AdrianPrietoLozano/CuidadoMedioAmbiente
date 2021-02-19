package com.example.cuidadodelambiente.ui.fragments.reportes;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.ui.base.MvpPresenter;
import com.example.cuidadodelambiente.ui.base.MvpView;

import java.util.List;

public interface Contract {
    interface View extends MvpView {
        void showReportes(List<UbicacionEvento> eventos);
        void showError(String error);
        void hideContenido();
    }

    interface Presenter<V extends View> extends MvpPresenter<V> {
        void fetchReportes();
        void onReportesFetched(List<UbicacionEvento> eventos);
        void onReportesError(String error);
    }

    interface Model {
        void fetchReportes();
    }
}
