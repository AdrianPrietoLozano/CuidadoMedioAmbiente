package com.example.cuidadodelambiente.ui.activities.crear_reporte.interactor;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.presenter.ICrearReportePresenter;

public class CrearReporteInteractor implements ICrearReporteInteractor {

    private ICrearReportePresenter presenter;

    public CrearReporteInteractor(ICrearReportePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void crearReporte(ReporteContaminacion reporte, String rutaImagen) {

    }

    @Override
    public void cancelarCrearReporte() {
        // call.cancel();
    }
}
