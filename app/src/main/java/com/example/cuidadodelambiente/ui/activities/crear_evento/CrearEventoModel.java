package com.example.cuidadodelambiente.ui.activities.crear_evento;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.FetchAddressIntentService;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearEventoModel implements Contract.Model {

    private Contract.Presenter presenter;
    private Call<CrearEventoResponse> callAgregarEvento;
    private CrearEventoModel.AddressResultReceiver resultReceiver;

    public CrearEventoModel(Contract.Presenter presenter) {
        this.presenter = presenter;
        resultReceiver = new AddressResultReceiver(new Handler());
    }

    @Override
    public void crearEvento(EventoLimpieza evento) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        callAgregarEvento = service.doAgregarEvento(evento.getIdReporte(), evento.getTitulo(),
                evento.getFecha(), evento.getHora(), evento.getDescripcion());

        callAgregarEvento.enqueue(new Callback<CrearEventoResponse>() {
            @Override
            public void onResponse(Call<CrearEventoResponse> call, Response<CrearEventoResponse> response) {
                if (!response.isSuccessful()) {
                    presenter.onEventoError("Not successful");
                    return;
                }

                CrearEventoResponse eResponse = response.body();
                if (eResponse.getStatus().getResultado() == 1) {
                    evento.setIdEvento(eResponse.getIdEvento());
                    presenter.onEventoCreado(evento);
                }
                else {
                    presenter.onEventoError(eResponse.getStatus().getMensaje());
                }
            }

            @Override
            public void onFailure(Call<CrearEventoResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.e("CANCELADO", "SE canceló la creación del evento");
                }

                presenter.onEventoError(t.getMessage());
            }
        });
    }

    @Override
    public void cancelarCrearEvento() {
        if (callAgregarEvento != null) {
            callAgregarEvento.cancel();
            presenter.onEventoCancelado();
        }
    }

    @Override
    public void getDireccionEvento(Context context, LatLng ubicacion) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(ubicacion.latitude);
        location.setLongitude(ubicacion.longitude);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        context.startService(intent);
    }
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String addressOutput;
            if (resultData == null || resultCode == Constants.FAILURE_RESULT) {
                //addressOutput = String.format("%s, %s", ubicacionReporte.latitude, ubicacionReporte.longitude);
                presenter.onDireccionError();

            } else {
                // Display the address string
                // or an error message sent from the intent service.
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) {
                    presenter.onDireccionError();
                } else {
                    presenter.onDireccionObtenida(addressOutput);
                }
            }
        }
    }

}
