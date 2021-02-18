package com.example.cuidadodelambiente.ui.activities.crear_evento;

import android.content.Context;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

public class CrearEventoPresenter implements Contract.Presenter {

    private Contract.View view;
    private Contract.Model model;

    public CrearEventoPresenter(@NotNull Contract.View view) {
        this.view = view;
        this.model = new CrearEventoModel(this);
    }

    @Override
    public void crearEvento(EventoLimpieza evento) {
        view.showLoading();
        model.crearEvento(evento);

    }

    @Override
    public void cancelarCrearEvento() {
        model.cancelarCrearEvento();
    }

    @Override
    public void onEventoCancelado() {
        view.onEventoCancelado();
    }

    @Override
    public void onEventoCreado(EventoLimpieza evento) {
        view.hideLoading();
        view.eventoCreado(evento);
    }

    @Override
    public void onEventoError(String error) {
        view.hideLoading();
        view.showError(error);
    }

    @Override
    public void getDireccionEvento(Context context, LatLng ubicacion) {
        view.showLoadingDireccion();
        model.getDireccionEvento(context, ubicacion);
    }

    @Override
    public void onDireccionObtenida(String direccion) {
        view.showDireccion(direccion);
    }

    @Override
    public void onDireccionError() {
        view.showErrorDireccion();
    }
}
