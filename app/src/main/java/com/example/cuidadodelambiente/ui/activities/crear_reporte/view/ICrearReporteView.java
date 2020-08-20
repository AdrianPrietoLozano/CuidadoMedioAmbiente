package com.example.cuidadodelambiente.ui.activities.crear_reporte.view;

public interface ICrearReporteView {
    public void onReporteCreadoExitosamente();
    public void onReporteCreadoError(String error);

    public void mostrarDialogoCarga();
    public void cerrarDialgoCarga();
}
