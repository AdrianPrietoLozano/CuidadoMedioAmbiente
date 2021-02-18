package com.example.cuidadodelambiente.ui.fragments.datos_evento.interactor;

public interface IDatosEventoInteractor {
    public void cargarDatosEvento(int idEvento);
    public void participarEnEvento(int idEvento);
    public void dejarDeParticiparEnEvento(int idEvento);
    public void cancelarCargarDatosEvento();
}
