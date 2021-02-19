package com.example.cuidadodelambiente.ui.fragments.eventos;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.ui.base.BasePresenter;

import java.util.List;

public class EventosPresenter<V extends Contract.View> extends BasePresenter<V> implements Contract.Presenter<V> {

    private Contract.Model model;

    public EventosPresenter() {
        this.model = new EventosModel(this);
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
    public void onEventosFetched(List<UbicacionEvento> eventos) {
        getView().hideLoading();
        getView().showEventos(eventos);
    }

    @Override
    public void onEventosError(String error) {
        getView().hideLoading();
        getView().showError(error);
    }
}
