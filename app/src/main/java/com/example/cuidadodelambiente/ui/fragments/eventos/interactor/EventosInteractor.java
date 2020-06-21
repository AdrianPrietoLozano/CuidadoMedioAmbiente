package com.example.cuidadodelambiente.ui.fragments.eventos.interactor;

import android.util.Log;

import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.eventos.presenter.EventosPresenter;

import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class EventosInteractor implements retrofit2.Callback<List<UbicacionEvento>> {

    private EventosPresenter presenter;

    public EventosInteractor(EventosPresenter presenter) {
        this.presenter = presenter;
    }

    public void getEventosDesdeServidor() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventos().enqueue(this);
    }

    @Override
    public void onResponse(Call<List<UbicacionEvento>> call, Response<List<UbicacionEvento>> response) {
        presenter.onConexionExitosa(response.body());
    }

    @Override
    public void onFailure(Call<List<UbicacionEvento>> call, Throwable t) {
        call.cancel();
        presenter.onConexionError();
        Log.e("ERROR CARGA EVENTOS", t.getMessage());
    }
}
