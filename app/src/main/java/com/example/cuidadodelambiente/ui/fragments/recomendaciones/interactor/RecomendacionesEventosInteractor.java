package com.example.cuidadodelambiente.ui.fragments.recomendaciones.interactor;


import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.IRecomendacionesEventosPresenter;

import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class RecomendacionesEventosInteractor implements retrofit2.Callback<List<EventoLimpieza>>,
        IRecomendacionesEventosInteractor {

    private IRecomendacionesEventosPresenter presenter;

    public RecomendacionesEventosInteractor(IRecomendacionesEventosPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void cargarRecomendacionesEventos(Integer id_usuario) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventosRecomendados(id_usuario).enqueue(this);
    }

    @Override
    public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
        if (response.isSuccessful()) {
            presenter.onConexionExitosa(response.body());
        } else {
            presenter.onConexionError(new Throwable());
        }
    }

    @Override
    public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
        presenter.onConexionError(t);
    }
}
