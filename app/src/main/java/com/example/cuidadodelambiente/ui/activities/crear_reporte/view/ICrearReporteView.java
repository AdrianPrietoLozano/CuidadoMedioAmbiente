package com.example.cuidadodelambiente.ui.activities.crear_reporte.view;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;

public interface ICrearReporteView {
    public void onReporteCreadoExitosamente(ReporteContaminacion reporte);
    public void onReporteCreadoError(String error);

    public void mostrarDialogoCarga();
    public void cerrarDialgoCarga();
}
