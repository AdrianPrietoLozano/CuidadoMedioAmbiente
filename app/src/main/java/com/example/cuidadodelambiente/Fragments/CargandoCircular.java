package com.example.cuidadodelambiente.Fragments;

import android.view.View;

public class CargandoCircular {
    private View pantallaCarga;
    private View contenidoPrincipal;

    public CargandoCircular(View contenidoPrincipal, View pantallaCarga){
        this.pantallaCarga = pantallaCarga;
        this.contenidoPrincipal = contenidoPrincipal;
    }

    public void ocultarCargaMostrarContenido()
    {
        ocultarPantallaCarga();
        mostrarContenidoPrincipal();
    }

    public void ocultarContenidoMostrarCarga()
    {
        ocultarContenidoPrincipal();
        mostrarPantallaCarga();
    }

    public void ocultarPantallaCarga()
    {
        pantallaCarga.setVisibility(View.GONE);
    }

    public void mostrarPantallaCarga()
    {
        pantallaCarga.setVisibility(View.VISIBLE);
    }

    public void mostrarContenidoPrincipal()
    {
        contenidoPrincipal.setVisibility(View.VISIBLE);
    }

    public void ocultarContenidoPrincipal()
    {
        contenidoPrincipal.setVisibility(View.INVISIBLE);
    }
}
