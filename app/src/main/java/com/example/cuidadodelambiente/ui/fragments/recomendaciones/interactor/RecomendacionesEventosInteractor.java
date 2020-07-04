package com.example.cuidadodelambiente.ui.fragments.recomendaciones.interactor;


import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.IRecomendacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.RecomendacionesEventosPresenter;

import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class RecomendacionesEventosInteractor implements retrofit2.Callback<List<EventoItem>>,
        IRecomendacionesEventosPresenter {

    private RecomendacionesEventosPresenter presenter;

    public RecomendacionesEventosInteractor(RecomendacionesEventosPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResponse(Call<List<EventoItem>> call, Response<List<EventoItem>> response) {
        presenter.onConexionExitosa(response.body());
    }

    @Override
    public void onFailure(Call<List<EventoItem>> call, Throwable t) {
        call.cancel();
        presenter.onConexionError();
    }

    @Override
    public void cargarRecomendacionesEventos(Integer id_usuario) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventosRecomendados(id_usuario).enqueue(this);
    }
}
