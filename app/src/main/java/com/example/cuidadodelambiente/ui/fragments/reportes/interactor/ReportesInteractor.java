package com.example.cuidadodelambiente.ui.fragments.reportes.interactor;

import com.example.cuidadodelambiente.data.models.UbicacionReporte;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.reportes.presenter.IReportesPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportesInteractor implements Callback<List<UbicacionReporte>>,
        IReportesInteractor {

    private IReportesPresenter presenter;

    public ReportesInteractor(IReportesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResponse(Call<List<UbicacionReporte>> call, Response<List<UbicacionReporte>> response) {
        presenter.onConexionExitosa(response.body());
    }

    @Override
    public void onFailure(Call<List<UbicacionReporte>> call, Throwable t) {
        presenter.onConexionError(t);
    }

    @Override
    public void cargarReportes() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetReportes().enqueue(this);
    }
}
