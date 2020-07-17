package com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter;

import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.interactor.IRecomendacionesEventosInteractor;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.interactor.RecomendacionesEventosInteractor;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.view.IRecomendacionesEventosView;

import java.util.List;

public class RecomendacionesEventosPresenter implements IRecomendacionesEventosPresenter{

    private IRecomendacionesEventosView view;
    private IRecomendacionesEventosInteractor interactor;

    public RecomendacionesEventosPresenter(IRecomendacionesEventosView view) {
        this.view = view;
        interactor = new RecomendacionesEventosInteractor(this);
    }

    @Override
    public void cargarRecomendacionesEventos(Integer id_usuario) {
        interactor.cargarRecomendacionesEventos(id_usuario);
    }

    @Override
    public void onConexionExitosa(List<EventoItem> eventos) {
        view.onEventosCargadosExitosamente(eventos);
    }

    @Override
    public void onConexionError(Throwable t) {
        view.onEventosCargadosError(t);
    }
}
