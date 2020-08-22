package com.example.cuidadodelambiente.ui.activities.crear_reporte.presenter;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;

public interface ICrearReportePresenter {
    public void crearReporte(ReporteContaminacion reporte, String rutaImagen);
    public void cancelarCrearReporte();
    public void onReporteCreadoExitosamente(ReporteContaminacion reporte);
    public void onReporteCreadoError(String error);
    public void onCrearReporteCancelado();
}
