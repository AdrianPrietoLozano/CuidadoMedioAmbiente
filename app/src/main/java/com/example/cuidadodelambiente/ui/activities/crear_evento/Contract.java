package com.example.cuidadodelambiente.ui.activities.crear_evento;

import android.content.Context;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.example.cuidadodelambiente.ui.base.MvpPresenter;
import com.example.cuidadodelambiente.ui.base.MvpView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface Contract {
    interface View extends MvpView {
        void eventoCreado(EventoLimpieza evento);
        void onEventoCancelado();
        void cerrar();
        void showDireccion(String direccion);
        void showLoadingDireccion();
        void showErrorDireccion();
    }

    interface Presenter<V extends View> extends MvpPresenter<V> {
        void crearEvento(EventoLimpieza evento);
        void cancelarCrearEvento();
        void onEventoCancelado();
        void onEventoCreado(EventoLimpieza evento);
        void onEventoError(String error);
        void getDireccionEvento(Context context, LatLng ubicacion);
        void onDireccionObtenida(String direccion);
        void onDireccionError();
    }

    interface Model {
        void crearEvento(EventoLimpieza evento);
        void cancelarCrearEvento();
        void getDireccionEvento(Context context, LatLng ubicacion);
    }
}
