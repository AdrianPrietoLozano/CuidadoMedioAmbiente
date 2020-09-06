package com.example.cuidadodelambiente.ui.fragments.datos_evento.interactor;

public interface IDatosEventoInteractor {
    public void cargarDatosEvento(int idEvento, int idUsuario);
    public void participarEnEvento(int idEvento, int idUsuario);
    public void dejarDeParticiparEnEvento(int idUsuario, int idEvento);
    public void cancelarCargarDatosEvento();
}
