package com.example.cuidadodelambiente.prueba;

import android.util.Log;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.google.gson.JsonObject;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UsuarioPrueba extends Thread {

    private int id;
    private ClaseObservadora observador;
    private APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);

    public UsuarioPrueba(int id, ClaseObservadora observador) {
        this.id = id;
        this.observador = observador;
    }

    public void crearEvento(int idReporte) {
        (new Runnable() {
            @Override
            public void run() {
                service.doAgregarEvento(idReporte, "evento",
                        "23/04/2021", "12:12", "des").enqueue(new Callback<CrearEventoResponse>() {
                    @Override
                    public void onResponse(Call<CrearEventoResponse> call, Response<CrearEventoResponse> response) {
                        observador.onResponseEvento(true, response.body().getIdEvento());
                    }

                    @Override
                    public void onFailure(Call<CrearEventoResponse> call, Throwable t) {
                        observador.onResponseEvento(false, null);
                    }
                });
            }
        }).run();
    }

    public void obtenerRecomendaciones() {
        (new Runnable() {

            @Override
            public void run() {
                service.doGetEventosRecomendados().enqueue(new Callback<List<EventoLimpieza>>() {
                    @Override
                    public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
                        observador.onResponseRecomendacion(true);
                    }

                    @Override
                    public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
                        observador.onResponseRecomendacion(false);
                    }
                });
            }
        }).run();

    }

    public void participarEnEvento(int idEvento) {
        (new Runnable() {

            @Override
            public void run() {
                service.doUnirseEvento(idEvento).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        observador.onResponseParticipacion(true);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        observador.onResponseParticipacion(false);
                    }
                });
            }
        }).run();

    }


    public interface ClaseObservadora {
        public void onResponseEvento(boolean exito, Integer idEvento);
        public void onResponseParticipacion(boolean exito);
        public void onResponseRecomendacion(boolean exito);

    }

}
