package com.example.cuidadodelambiente.ui.activities.crear_evento;

import android.content.Context;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.example.cuidadodelambiente.ui.base.BasePresenter;
import com.google.android.gms.maps.model.LatLng;

public class CrearEventoPresenter<V extends Contract.View> extends BasePresenter<V> implements Contract.Presenter<V> {

    private Contract.Model model;

    public CrearEventoPresenter() {
        this.model = new CrearEventoModel(this);
    }

    @Override
    public void crearEvento(EventoLimpieza evento) {
        getView().showLoading();
        //model.crearEvento(evento);

    }

    @Override
    public void cancelarCrearEvento() {
        model.cancelarCrearEvento();
    }

    @Override
    public void onEventoCancelado() {
        getView().hideLoading();
        getView().onEventoCancelado();
    }

    @Override
    public void onEventoCreado(EventoLimpieza evento) {
        getView().hideLoading();
        getView().eventoCreado(evento);
    }

    @Override
    public void onEventoError(String error) {
        getView().hideLoading();
        getView().showError(error);
    }

    @Override
    public void getDireccionEvento(Context context, LatLng ubicacion) {
        getView().showLoadingDireccion();
        model.getDireccionEvento(context, ubicacion);
    }

    @Override
    public void onDireccionObtenida(String direccion) {
        getView().showDireccion(direccion);
    }

    @Override
    public void onDireccionError() {
        getView().showErrorDireccion();
    }
}
