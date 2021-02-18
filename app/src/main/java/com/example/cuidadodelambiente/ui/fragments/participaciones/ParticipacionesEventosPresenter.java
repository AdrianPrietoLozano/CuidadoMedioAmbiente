package com.example.cuidadodelambiente.ui.fragments.participaciones;

import com.example.cuidadodelambiente.AppContext;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.ui.fragments.participaciones.Contract;
import com.example.cuidadodelambiente.ui.fragments.participaciones.ParticipacionesEventosModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParticipacionesEventosPresenter implements Contract.Presenter {

    private Contract.Model model;
    private Contract.View view;

    public ParticipacionesEventosPresenter(@NotNull Contract.View view) {
        this.view = view;
        this.model = new ParticipacionesEventosModel(this);
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
