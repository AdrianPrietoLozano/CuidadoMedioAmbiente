package com.example.cuidadodelambiente.ui.fragments.datos_evento.interactor;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.presenter.IDatosEventoPresenter;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;

public class DatosEventoInteractor implements IDatosEventoInteractor {

    private IDatosEventoPresenter presenter;
    private Call<EventoLimpieza> callDatosEvento;

    public DatosEventoInteractor(IDatosEventoPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void cargarDatosEvento(int idEvento, int idUsuario) {

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        callDatosEvento = service.doGetEventoLimpieza(idEvento, idUsuario);
        callDatosEvento.enqueue(new Callback<EventoLimpieza>() {
            @Override
            public void onResponse(Call<EventoLimpieza> call, retrofit2.Response<EventoLimpieza> response) {

                if (!response.isSuccessful()) {
                    presenter.onCargarDatosEventoError("Ocurrió un error");
                    return;
                }

                EventoLimpieza eventoLimpieza = response.body();
                if (eventoLimpieza.getResultado() == 1) {
                    presenter.onCargarDatosEventoExito(eventoLimpieza);
                } else {
                    presenter.onCargarDatosEventoError(eventoLimpieza.getMensaje());
                }
            }

            @Override
            public void onFailure(Call<EventoLimpieza> call, Throwable throwable) {
                presenter.onCargarDatosEventoError(throwable.getMessage());
            }
        });
    }

    @Override
    public void participarEnEvento(int idEvento, int idUsuario) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> callUnirseEvento = service.doUnirseEvento(idUsuario, idEvento);

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
        callDatosEvento.cancel();
    }

    @Override
    public void dejarDeParticiparEnEvento(int idUsuario, int idEvento) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.doDejarParticiparEvento(idUsuario, idEvento);

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
