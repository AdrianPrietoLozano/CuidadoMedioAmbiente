package com.example.cuidadodelambiente.ui.fragments.datos_evento;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.EventoLimpiezaResponse;
import com.example.cuidadodelambiente.data.responses.StatusResponse;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatosEventoModel implements Contract.Model {

    private Contract.Presenter presenter;
    private APIInterface service;

    public DatosEventoModel(Contract.Presenter presenter) {
        this.presenter = presenter;
        service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
    }

    @Override
    public void fetchEvento(int idEvento) {
        Call<EventoLimpiezaResponse> call = service.doGetEventoLimpieza(idEvento);
        call.enqueue(new Callback<EventoLimpiezaResponse>() {
            @Override
            public void onResponse(Call<EventoLimpiezaResponse> call, retrofit2.Response<EventoLimpiezaResponse> response) {

                if (!response.isSuccessful()) {
                    presenter.onFetchEventoError("Ocurrió un error");
                    return;
                }

                StatusResponse status = response.body().getStatus();
                if (status.getResultado() == 1) {
                    presenter.onEventoFetched(response.body().getEvento());
                } else {
                    presenter.onFetchEventoError(status.getMensaje());
                }
            }

            @Override
            public void onFailure(Call<EventoLimpiezaResponse> call, Throwable throwable) {
                presenter.onFetchEventoError(throwable.getMessage());
            }
        });
    }

    @Override
    public void participarEnEvento(int idEvento) {
        Call<JsonObject> callUnirseEvento = service.doUnirseEvento(idEvento);

        callUnirseEvento.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    presenter.onParticiparEnEventoError("Ocurrió un error");
                    return;
                }

                JsonObject json = response.body();
                int resultado = json.get("resultado").getAsInt();

                if (resultado != 1) {
                    presenter.onParticiparEnEventoError(json.get("mensaje").getAsString());
                    return;
                }

                presenter.onParticiparEnEventoExito();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                presenter.onParticiparEnEventoError(t.getMessage());
            }
        });
    }

    @Override
    public void dejarDeParticiparEnEvento(int idEvento) {
        Call<JsonObject> call = service.doDejarParticiparEvento(idEvento);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    presenter.onDejarParticiparEventoError("Ocurrió un error");
                    return;
                }

                JsonObject json = response.body();
                int resultado = json.get("resultado").getAsInt();

                if (resultado != 1) {
                    presenter.onDejarParticiparEventoError(json.get("mensaje").getAsString());
                    return;
                }

                presenter.onDejarParticiparEventoExito();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                presenter.onDejarParticiparEventoError(t.getMessage());
            }
        });
    }

    @Override
    public void fetchRecomendacionesEvento(int idEvento) {
        Call<List<EventoLimpieza>> call = service.doGetRecomendacionesEvento(idEvento);
        call.enqueue(new Callback<List<EventoLimpieza>>() {
            @Override
            public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
                if (!response.isSuccessful()) {
                    presenter.onRecomendacionesError("Not successful");
                    return;
                }

                presenter.onRecomnedacionesFetched(response.body());
            }

            @Override
            public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
                presenter.onRecomendacionesError(t.getMessage());
            }
        });
    }
}
