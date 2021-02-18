package com.example.cuidadodelambiente.ui.fragments.recomendaciones;

import com.example.cuidadodelambiente.AppContext;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;

import java.util.List;

public class RecomendacionesEventosPresenter implements Contract.Presenter {

    private Contract.View view;
    private Contract.Model model;

    public RecomendacionesEventosPresenter(Contract.View view) {
        this.view = view;
        this.model = new RecomendacionesEventosModel(this);
    }


    @Override
    public void fetchEventos() {
        if (Utilidades.hayConexionInternet(AppContext.getContext())) {
            view.showLoading();
            model.fetchEventos();
        } else {
            view.showError("Sin conexión a internet");
        }
    }

    @Override
    public void onEventosFetched(List<EventoLimpieza> eventos) {
        if (eventos.isEmpty()) {
            view.showNoEventos();
        } else {
            view.showEventos(eventos);
        }
    }

    @Override
    public void onEventosError(String error) {
        view.showError(error);
    }
}
