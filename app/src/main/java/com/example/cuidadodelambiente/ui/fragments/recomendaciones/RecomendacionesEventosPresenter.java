package com.example.cuidadodelambiente.ui.fragments.recomendaciones;

import com.example.cuidadodelambiente.AppContext;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.ui.base.BasePresenter;

import java.util.List;

public class RecomendacionesEventosPresenter<V extends Contract.View> extends BasePresenter<V>
    implements Contract.Presenter<V>{

    private Contract.Model model;

    public RecomendacionesEventosPresenter() {
        this.model = new RecomendacionesEventosModel(this);
    }

    @Override
    public void fetchEventos() {
        getView().hideContenido();
        getView().hideError();
        if (getView().isNetworkConnected()) {
            getView().showLoading();
            model.fetchEventos();
        } else {
            getView().showError("Sin conexión a internet");
        }
    }

    @Override
    public void onEventosFetched(List<EventoLimpieza> eventos) {
        getView().hideLoading();
        if (eventos.isEmpty()) {
            getView().showNoEventos();
        } else {
            getView().showEventos(eventos);
        }
    }

    @Override
    public void onEventosError(String error) {
        getView().hideLoading();
        getView().showError(error);
    }
}
