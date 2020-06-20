package com.example.cuidadodelambiente.ui.fragments.reportes.interactor;

import com.example.cuidadodelambiente.data.models.UbicacionReporte;

import java.util.List;

public interface IReportesInteractor {
    void onConexionExitosa(List<UbicacionReporte> reportes);
    void onConexionError();
}
