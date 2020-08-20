package com.example.cuidadodelambiente.ui.activities.crear_reporte.presenter;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.interactor.CrearReporteInteractor;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.interactor.ICrearReporteInteractor;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.view.ICrearReporteView;

public class CrearReportePresenter implements ICrearReportePresenter {

    private ICrearReporteView view;
    private ICrearReporteInteractor interactor;

    public CrearReportePresenter(ICrearReporteView view) {
        this.view = view;
        interactor = new CrearReporteInteractor(this);
    }

    @Override
    public void crearReporte(ReporteContaminacion reporte, String rutaImagen) {
        view.mostrarDialogoCarga();
        interactor.crearReporte(reporte, rutaImagen);
    }

    @Override
    public void cancelarCrearReporte() {
        interactor.cancelarCrearReporte();
    }

    @Override
    public void onReporteCreadoExitosamente() {
        view.cerrarDialgoCarga();
        view.onReporteCreadoExitosamente();
    }

    @Override
    public void onReporteCreadoError(String error) {
        view.cerrarDialgoCarga();
        view.onReporteCreadoError(error);
    }

    @Override
    public void onCrearReporteCancelado() {
        view.cerrarDialgoCarga();
    }
}
