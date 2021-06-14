package com.example.cuidadodelambiente.ui.activities.ranking;

import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.EventoLimpiezaResponse;
import com.example.cuidadodelambiente.data.responses.RankingResponse;
import com.example.cuidadodelambiente.data.responses.StatusResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class RankingModel implements Contract.Model {
    private Contract.Presenter presenter;
    private APIInterface service;

    public RankingModel(Contract.Presenter presenter) {
        this.presenter = presenter;
        service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
    }

    @Override
    public void fetchRanking() {
        Call<RankingResponse> call = service.doGetRanking();
        call.enqueue(new Callback<RankingResponse>() {
            @Override
            public void onResponse(Call<RankingResponse> call, retrofit2.Response<RankingResponse> response) {

                if (!response.isSuccessful()) {
                    presenter.onfetchRankingError("Ocurrió un error");
                    return;
                }

                StatusResponse status = response.body().getEstatus();
                if (status.getResultado() == 1) {
                    presenter.onfetchRankingExito(response.body().getRanking());
                } else {
                    presenter.onfetchRankingError(status.getMensaje());
                }
            }

            @Override
            public void onFailure(Call<RankingResponse> call, Throwable throwable) {
                presenter.onfetchRankingError(throwable.getMessage());
            }
        });
    }
}
