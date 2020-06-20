package com.example.cuidadodelambiente.ui.fragments.reportes.interactor;

import com.example.cuidadodelambiente.data.models.UbicacionReporte;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportesInteractor implements Callback<List<UbicacionReporte>> {

    private IReportesInteractor listener;
    private RetrofitClientInstance retrofiClient;

    public ReportesInteractor(IReportesInteractor listener) {
        this.listener = listener;
        retrofiClient = new RetrofitClientInstance();
    }

    public void getReportesDesdeServidor() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetReportes().enqueue(this);

    }

    @Override
    public void onResponse(Call<List<UbicacionReporte>> call, Response<List<UbicacionReporte>> response) {
        if(response.body() == null) {
            listener.onConexionError();
        }
        else {
            listener.onConexionExitosa(response.body());
        }
    }

    @Override
    public void onFailure(Call<List<UbicacionReporte>> call, Throwable t) {
        call.cancel();
        listener.onConexionError();
    }
}
