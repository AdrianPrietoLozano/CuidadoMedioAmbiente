package com.example.cuidadodelambiente.ui.fragments.limpiezas;

import com.example.cuidadodelambiente.data.responses.CrearLimpiezaResponse;
import com.example.cuidadodelambiente.ui.BasePresenter;

public interface Contract {

    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String msg);
        void onLimpiezaCreada();
        void close();
    }

    interface Presenter extends BasePresenter {
        void crearLimpieza(Integer idReporte, String descripcion, String urlFoto);
        void onLimpiezaCreada(CrearLimpiezaResponse response);
        void onLimpiezaError(String error);
    }

    interface Model {
        void crearLimpieza(Integer idReporte, String descripcion, String urlFoto);
    }
}
