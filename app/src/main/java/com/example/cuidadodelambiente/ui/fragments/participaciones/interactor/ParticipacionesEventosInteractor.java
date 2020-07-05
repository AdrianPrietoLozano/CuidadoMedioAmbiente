package com.example.cuidadodelambiente.ui.fragments.participaciones.interactor;

import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.ParticipaEventoItem;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.participaciones.presenter.IParticipacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.interactor.IRecomendacionesEventosInteractor;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.IRecomendacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.RecomendacionesEventosPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipacionesEventosInteractor implements Callback<List<EventoLimpieza>>,
        IParticipacionesEventosPresenter {

    private IParticipacionesEventosInteractor listener;

    public ParticipacionesEventosInteractor(IParticipacionesEventosInteractor listener) {
        this.listener = listener;
    }

    @Override
    public void cargarParticipacionesEventos(Integer idUsuario) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<List<EventoLimpieza>> call = service.doGetEventosParticipa(idUsuario);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
        this.listener.onConexionExitosa(response.body());
    }

    @Override
    public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
        call.cancel();
        this.listener.onConexionError();
    }
}
