package com.example.cuidadodelambiente.ui.fragments.reportes.presenter;

import com.example.cuidadodelambiente.data.models.UbicacionReporte;

import java.util.List;

public interface IReportesPresenter {
    void cargarReportes();
    void onConexionExitosa(List<UbicacionReporte> reportes);
    void onConexionError(Throwable t);
}
