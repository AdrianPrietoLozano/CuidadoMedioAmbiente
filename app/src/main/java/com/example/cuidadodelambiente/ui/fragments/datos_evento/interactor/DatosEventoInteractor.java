package com.example.cuidadodelambiente.ui.fragments.datos_evento.interactor;

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
import com.example.cuidadodelambiente.ui.fragments.datos_evento.presenter.IDatosEventoPresenter;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;

public class DatosEventoInteractor implements IDatosEventoInteractor {

    private IDatosEventoPresenter presenter;
    private Call<EventoLimpiezaResponse> callDatosEvento;

    public DatosEventoInteractor(IDatosEventoPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void cargarDatosEvento(int idEvento) {

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        callDatosEvento = service.doGetEventoLimpieza(idEvento);
        callDatosEvento.enqueue(new Callback<EventoLimpiezaResponse>() {
            @Override
            public void onResponse(Call<EventoLimpiezaResponse> call, retrofit2.Response<EventoLimpiezaResponse> response) {

                if (!response.isSuccessful()) {
                    presenter.onCargarDatosEventoError("Ocurrió un error");
                    return;
                }

                StatusResponse status = response.body().getStatus();
                if (status.getResultado() == 1) {
                    presenter.onCargarDatosEventoExito(response.body().getEvento());
                } else {
                    presenter.onCargarDatosEventoError(status.getMensaje());
                }
            }

            @Override
            public void onFailure(Call<EventoLimpiezaResponse> call, Throwable throwable) {
                presenter.onCargarDatosEventoError(throwable.getMessage());
            }
        });
    }

    @Override
    public void participarEnEvento(int idEvento) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> callUnirseEvento = service.doUnirseEvento(idEvento);

        callUnirseEvento.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    presenter.onParticiparEnEventoError(0, "Ocurrió un error");
                    return;
                }

                JsonObject json = response.body();
                int resultado = json.get("resultado").getAsInt();

                if (resultado != 1) {
                    presenter.onParticiparEnEventoError(resultado, json.get("mensaje").getAsString());
                    return;
                }

                presenter.onParticiparEnEventoExito();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                presenter.onParticiparEnEventoError(0, t.getMessage());
            }
        });
    }

    @Override
    public void cancelarCargarDatosEvento() {
        if (callDatosEvento != null) {
            callDatosEvento.cancel();
        }
    }

    @Override
    public void dejarDeParticiparEnEvento(int idEvento) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.doDejarParticiparEvento(idEvento);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    presenter.onDejarParticiparEventoError(0, "Ocurrió un error");
                    return;
                }

                JsonObject json = response.body();
                int resultado = json.get("resultado").getAsInt();

                if (resultado != 1) {
                    presenter.onDejarParticiparEventoError(resultado, json.get("mensaje").getAsString());
                    return;
                }

                presenter.onDejarParticiparEventoExito();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                presenter.onDejarParticiparEventoError(0, t.getMessage());
            }
        });
    }
}
