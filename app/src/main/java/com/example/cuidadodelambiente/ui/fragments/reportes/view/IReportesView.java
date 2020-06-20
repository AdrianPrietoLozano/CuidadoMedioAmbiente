package com.example.cuidadodelambiente.ui.fragments.reportes.view;

import com.example.cuidadodelambiente.data.models.UbicacionReporte;

import java.util.List;

public interface IReportesView {
    void onReportesRecibidosCorrectamente(List<UbicacionReporte> reportes);
    void onReportesRecibidosError();
}
