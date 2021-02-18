package com.example.cuidadodelambiente.ui.fragments.datos_evento.presenter;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.interactor.DatosEventoInteractor;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.interactor.IDatosEventoInteractor;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.IDatosEventoView;

public class DatosEventoPresenter implements IDatosEventoPresenter {

    private IDatosEventoInteractor interactor;
    private IDatosEventoView view;

    public DatosEventoPresenter(IDatosEventoView view) {
        this.view = view;
        this.interactor = new DatosEventoInteractor(this);
    }

    @Override
    public void cargarDatosEvento(int idEvento) {
        interactor.cargarDatosEvento(idEvento);
    }

    @Override
    public void participarEnEvento(int idEvento) {
        interactor.participarEnEvento(idEvento);
    }

    @Override
    public void dejarDeParticiparEnEvento(int idEvento) {
        interactor.dejarDeParticiparEnEvento(idEvento);
    }

    @Override
    public void cancelarCargarDatosEvento() {
        interactor.cancelarCargarDatosEvento();
    }

    @Override
    public void onCargarDatosEventoError(String error) {
        view.onCargarDatosEventoError(error);
    }

    @Override
    public void onParticiparEnEventoError(int resultado, String error) {
        view.onParticiparEnEventoError(resultado, error);
    }

    @Override
    public void onDejarParticiparEventoError(int resultado, String error) {
        view.onDejarParticiparEventoError(resultado, error);
    }

    @Override
    public void onCargarDatosEventoExito(EventoLimpieza evento) {
        view.onCargarDatosEventoExito(evento);
    }

    @Override
    public void onParticiparEnEventoExito() {
        view.onParticiparEnEventoExito();
    }

    @Override
    public void onDejarParticiparEventoExito() {
        view.onDejarParticiparEventoExito();
    }
}
