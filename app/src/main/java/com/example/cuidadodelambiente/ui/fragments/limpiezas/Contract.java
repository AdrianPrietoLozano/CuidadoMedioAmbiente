package com.example.cuidadodelambiente.ui.fragments.limpiezas;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cuidadodelambiente.data.responses.CrearLimpiezaResponse;
import com.example.cuidadodelambiente.ui.BasePresenter;
import com.google.android.gms.maps.model.LatLng;

public interface Contract {

    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String msg);
        void onLimpiezaCreada();
        void close();
    }

    interface Presenter extends BasePresenter {
        void crearLimpieza(Integer idReporte, String descripcion, String urlFoto, LatLng ubicacion);
        void onLimpiezaCreada(CrearLimpiezaResponse response);
        void onLimpiezaError(String error);
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    }

    interface Model {
        void crearLimpieza(Integer idReporte, String descripcion, String urlFoto);
    }
}
