package com.example.cuidadodelambiente.ui.fragments.participaciones.presenter;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.ui.fragments.participaciones.interactor.ParticipacionesEventosInteractor;
import com.example.cuidadodelambiente.ui.fragments.participaciones.view.IParticipacionesEventosView;
import com.example.cuidadodelambiente.ui.fragments.participaciones.interactor.IParticipacionesEventosInteractor;

import java.util.List;

public class ParticipacionesEventosPresenter implements IParticipacionesEventosPresenter {

    private IParticipacionesEventosInteractor interactor;
    private IParticipacionesEventosView view;

    public ParticipacionesEventosPresenter(IParticipacionesEventosView view) {
        this.view = view;
        this.interactor = new ParticipacionesEventosInteractor(this);
    }

    @Override
    public void cargarParticipacionesEventos() {
        this.interactor.cargarParticipacionesEventos();
    }

    @Override
    public void onConexionExitosa(List<EventoLimpieza> eventos) {
        view.onEventosCargadosExitosamente(eventos);
    }

    @Override
    public void onConexionError(Throwable t) {
        view.onEventosCargadosError(t);
    }
}
