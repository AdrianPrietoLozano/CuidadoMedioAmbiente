package com.example.cuidadodelambiente.ui.fragments.participaciones;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.participaciones.Contract;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipacionesEventosModel implements Callback<List<EventoLimpieza>>,
        Contract.Model {

    private Contract.Presenter presenter;

    public ParticipacionesEventosModel(@NotNull Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
        if (response.isSuccessful()) {
            this.presenter.onEventosFetched(response.body());
        } else {
            presenter.onEventosError("Not successful");
        }
    }

    @Override
    public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
        this.presenter.onEventosError(t.getMessage());
    }

    @Override
    public void fetchEventos() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<List<EventoLimpieza>> call = service.doGetEventosParticipa();
        call.enqueue(this);
    }
}
