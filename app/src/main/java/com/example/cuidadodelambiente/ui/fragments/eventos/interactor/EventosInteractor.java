package com.example.cuidadodelambiente.ui.fragments.eventos.interactor;

import android.util.Log;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.eventos.presenter.EventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.eventos.presenter.IEventosPresenter;

import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class EventosInteractor implements retrofit2.Callback<List<UbicacionEvento>>,
        IEventosInteractor {

    private IEventosPresenter presenter;

    public EventosInteractor(IEventosPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResponse(Call<List<UbicacionEvento>> call, Response<List<UbicacionEvento>> response) {
        presenter.onConexionExitosa(response.body());
    }

    @Override
    public void onFailure(Call<List<UbicacionEvento>> call, Throwable t) {
        presenter.onConexionError(t);
    }

    @Override
    public void cargarEventos() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventos().enqueue(this);
    }
}
