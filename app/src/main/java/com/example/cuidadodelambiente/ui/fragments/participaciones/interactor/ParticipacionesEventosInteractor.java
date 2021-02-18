package com.example.cuidadodelambiente.ui.fragments.participaciones.interactor;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.participaciones.presenter.IParticipacionesEventosPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipacionesEventosInteractor implements Callback<List<EventoLimpieza>>,
        IParticipacionesEventosInteractor {

    private IParticipacionesEventosPresenter presenter;

    public ParticipacionesEventosInteractor(IParticipacionesEventosPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void cargarParticipacionesEventos() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<List<EventoLimpieza>> call = service.doGetEventosParticipa();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
        if (response.isSuccessful()) {
            this.presenter.onConexionExitosa(response.body());
        } else {
            presenter.onConexionError(new Throwable());
        }
    }

    @Override
    public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
        this.presenter.onConexionError(t);
    }
}
