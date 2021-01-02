package com.example.cuidadodelambiente.helpers;

import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HelperCargaError {
    private View pantallaCarga;
    private View contenidoPrincipal;
    private View pantallaError;

    public HelperCargaError(View contenidoPrincipal, View pantallaCarga, View pantallaError){
        this.pantallaCarga = pantallaCarga;
        this.contenidoPrincipal = contenidoPrincipal;
        this.pantallaError = pantallaError;
    }

    public void mostrarPantallaCarga()
    {
        pantallaError.setVisibility(View.GONE);
        contenidoPrincipal.setVisibility(View.GONE);
        pantallaCarga.setVisibility(View.VISIBLE);
    }

    public void mostrarContenidoPrincipal()
    {
        pantallaError.setVisibility(View.GONE);
        pantallaCarga.setVisibility(View.GONE);
        contenidoPrincipal.setVisibility(View.VISIBLE);
    }

    public void mostrarPantallaError() {
        pantallaCarga.setVisibility(View.GONE);
        contenidoPrincipal.setVisibility(View.GONE);
        pantallaError.setVisibility(View.VISIBLE);
    }
}
