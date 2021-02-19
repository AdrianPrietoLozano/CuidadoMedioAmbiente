package com.example.cuidadodelambiente.ui.fragments.eventos;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.eventos.interactor.IEventosInteractor;
import com.example.cuidadodelambiente.ui.fragments.eventos.presenter.IEventosPresenter;

import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class EventosModel implements retrofit2.Callback<List<UbicacionEvento>>,
        Contract.Model {

    private Contract.Presenter presenter;

    public EventosModel(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResponse(Call<List<UbicacionEvento>> call, Response<List<UbicacionEvento>> response) {
        if (response.isSuccessful()) {
            presenter.onEventosFetched(response.body());
        } else {
            presenter.onEventosError("Not successful");
        }
    }

    @Override
    public void onFailure(Call<List<UbicacionEvento>> call, Throwable t) {
        presenter.onEventosError(t.getMessage());
    }

    @Override
    public void fetchEventos() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventos().enqueue(this);
    }
}
