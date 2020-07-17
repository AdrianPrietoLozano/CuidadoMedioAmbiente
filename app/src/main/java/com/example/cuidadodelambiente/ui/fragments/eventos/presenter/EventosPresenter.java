package com.example.cuidadodelambiente.ui.fragments.eventos.presenter;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.ui.fragments.eventos.interactor.EventosInteractor;
import com.example.cuidadodelambiente.ui.fragments.eventos.interactor.IEventosInteractor;
import com.example.cuidadodelambiente.ui.fragments.eventos.view.IEventosView;

import java.util.List;

public class EventosPresenter implements IEventosPresenter {

    private IEventosInteractor interactor;
    private IEventosView view;

    public EventosPresenter(IEventosView view) {
        this.view = view;
        this.interactor = new EventosInteractor(this);
    }

    @Override
    public void cargarEventos() {
        interactor.cargarEventos();
    }

    @Override
    public void onConexionExitosa(List<UbicacionEvento> eventos) {
        view.onEventosCargadosExitosamente(eventos);
    }

    @Override
    public void onConexionError(Throwable t) {
        view.onEventosCargadosError(t);
    }
}
