package com.example.cuidadodelambiente.ui.activities.crear_limpieza;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cuidadodelambiente.data.network.ActualizacionesUbicacionHelper;
import com.example.cuidadodelambiente.data.responses.CrearLimpiezaResponse;
import com.google.android.gms.maps.model.LatLng;

public class LimpiezaPresenter implements Contract.Presenter,
        ActualizacionesUbicacionHelper.OnActualizacionUbicacion {

    private Contract.View view;
    private Contract.Model model;
    private ActualizacionesUbicacionHelper actualizacionesUbicacion;
    private static final int MIN_DISTANCIA_LIMPIEZA = 100; // metros

    private Integer idReporte;
    private String descripcion;
    private String urlFoto;
    private LatLng ubicacion;

    public LimpiezaPresenter(Contract.View view) {
        this.view = view;
        this.model = new LimpiezaModel(this);
        actualizacionesUbicacion = new ActualizacionesUbicacionHelper(view.getActividad(), this, 60 * (60 * 1000));
    }

    @Override
    public void crearLimpieza(Integer idReporte, String descripcion, String urlFoto, LatLng ubicacion) {
        if (view == null) return;

        view.showLoading();

        this.idReporte = idReporte;
        this.descripcion = descripcion;
        this.urlFoto = urlFoto;
        this.ubicacion = ubicacion;

        actualizacionesUbicacion.iniciarObtenerUbicacion();
    }

    @Override
    public void onLimpiezaCreada(CrearLimpiezaResponse response) {
        if (view == null) return;

        view.hideLoading();
        view.showMessage("Limpieza creada correctamente");
        view.onLimpiezaCreada();
        view.close();
    }

    @Override
    public void onLimpiezaError(String error) {
        if (view == null) return;

        view.hideLoading();
        view.showMessage(error);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        actualizacionesUbicacion.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        actualizacionesUbicacion.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void detachView() {
        this.view = null;
        if (actualizacionesUbicacion != null)
            actualizacionesUbicacion.desconectar();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("CHANGE", "Location changed");
        if (view == null) return;

        float[] results = new float[5];
        Location.distanceBetween(this.ubicacion.latitude, this.ubicacion.longitude,
                location.getLatitude(), location.getLongitude(), results);
        float distancia = results[0];
        if (distancia < MIN_DISTANCIA_LIMPIEZA) {
            Log.e("PRESENTER", "Iniciando crear limpieza");
            model.crearLimpieza(idReporte, descripcion, urlFoto);
        } else {
            view.hideLoading();
            view.showMessage("Error. Estas muy lejos del punto contaminado (" + String.valueOf(distancia) + "mts)");
        }

        actualizacionesUbicacion.desconectar();
    }

    @Override
    public void onConnectionFailed() {
        Log.e("onConnection","error en conectar");
        if (view == null) return;

        view.hideLoading();
        view.showMessage("OnConnectionFailed");
    }

    @Override
    public void onPermissionError(String error) {
        Log.e("PERMISION", error);
        if (view == null) return;

        view.hideLoading();
        view.showMessage("OnPermissionError");

    }

    @Override
    public void onError(String error) {
        Log.e("onError", error);
        if (view == null) return;

        view.hideLoading();
        view.showMessage("OnError");
    }
}
