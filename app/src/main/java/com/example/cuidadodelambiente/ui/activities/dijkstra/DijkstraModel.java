package com.example.cuidadodelambiente.ui.activities.dijkstra;

import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.DijkstraResponse;
import com.example.cuidadodelambiente.data.responses.RankingResponse;
import com.example.cuidadodelambiente.data.responses.StatusResponse;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;

public class DijkstraModel implements Contract.Model {
    private Contract.Presenter presenter;
    private APIInterface service;

    public DijkstraModel(Contract.Presenter presenter) {
        this.presenter = presenter;
        service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
    }

    @Override
    public void fetchDijkstra(LatLng ubicacion, Integer puntos) {
        Call<DijkstraResponse> call = service.doGetRutaDijkstra(ubicacion.latitude, ubicacion.longitude, puntos);
        call.enqueue(new Callback<DijkstraResponse>() {
            @Override
            public void onResponse(Call<DijkstraResponse> call, retrofit2.Response<DijkstraResponse> response) {

                if (!response.isSuccessful()) {
                    presenter.onFetchDijkstraError("Ocurrió un error");
                    return;
                }

                StatusResponse status = response.body().getEstatus();
                if (status.getResultado() == 1) {
                    presenter.onFetchDijkstraExito(response.body().getRuta());
                } else {
                    presenter.onFetchDijkstraError(status.getMensaje());
                }
            }

            @Override
            public void onFailure(Call<DijkstraResponse> call, Throwable throwable) {
                presenter.onFetchDijkstraError(throwable.getMessage());
            }
        });
    }
}
