package com.example.cuidadodelambiente.ui.activities.crear_reporte.interactor;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;

public interface ICrearReporteInteractor {
    public void crearReporte(ReporteContaminacion reporte, String rutaImagen);
    public void cancelarCrearReporte();
}
