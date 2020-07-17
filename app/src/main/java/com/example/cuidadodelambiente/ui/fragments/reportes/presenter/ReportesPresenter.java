package com.example.cuidadodelambiente.ui.fragments.reportes.presenter;

import com.example.cuidadodelambiente.data.models.UbicacionReporte;
import com.example.cuidadodelambiente.ui.fragments.reportes.interactor.IReportesInteractor;
import com.example.cuidadodelambiente.ui.fragments.reportes.interactor.ReportesInteractor;
import com.example.cuidadodelambiente.ui.fragments.reportes.view.IReportesView;

import java.util.List;

public class ReportesPresenter implements IReportesPresenter {

    private IReportesView view;
    private IReportesInteractor interactor;

    public ReportesPresenter(IReportesView view) {
        this.view = view;
        this.interactor = new ReportesInteractor(this);
    }

    @Override
    public void cargarReportes() {
        interactor.cargarReportes();
    }

    @Override
    public void onConexionExitosa(List<UbicacionReporte> reportes) {
        view.onReportesRecibidosCorrectamente(reportes);
    }

    @Override
    public void onConexionError(Throwable t) {
        view.onReportesRecibidosError(t);
    }
}
