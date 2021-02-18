package com.example.cuidadodelambiente.ui.fragments.recomendaciones;


import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;

import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class RecomendacionesEventosModel implements retrofit2.Callback<List<EventoLimpieza>>,
        Contract.Model {

    private Contract.Presenter presenter;

    public RecomendacionesEventosModel(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
        if (response.isSuccessful()) {
            presenter.onEventosFetched(response.body());
        } else {
            presenter.onEventosError("Not successful");
        }
    }

    @Override
    public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
        presenter.onEventosError(t.getMessage());
    }

    @Override
    public void fetchEventos() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventosRecomendados().enqueue(this);
    }
}
