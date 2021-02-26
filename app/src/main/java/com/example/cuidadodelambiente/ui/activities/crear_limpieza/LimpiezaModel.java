package com.example.cuidadodelambiente.ui.activities.crear_limpieza;

import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.CrearLimpiezaResponse;
import com.example.cuidadodelambiente.data.responses.StatusResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LimpiezaModel implements Contract.Model {
    private Contract.Presenter presenter;
    private APIInterface service;

    public LimpiezaModel(Contract.Presenter presenter) {
        this.presenter = presenter;
        service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface .class);
    }

    @Override
    public void crearLimpieza(Integer idReporte, String descripcion, String urlFoto) {
        RequestBody idReportePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idReporte));

        MultipartBody.Part imagenPart = null;
        if (urlFoto != null) {
            File file = new File(urlFoto);
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            imagenPart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        }

        RequestBody descripcionPart = null;
        if (!descripcion.equals("")) {
            descripcionPart = RequestBody.create(MediaType.parse("text/plain"), descripcion);
        }

        Call<CrearLimpiezaResponse> call = service.doAgregarLimpieza(idReportePart, descripcionPart, imagenPart);
        call.enqueue(new Callback<CrearLimpiezaResponse>() {
            @Override
            public void onResponse(Call<CrearLimpiezaResponse> call, Response<CrearLimpiezaResponse> response) {
                if (!response.isSuccessful()) {
                    presenter.onLimpiezaError("Not successful");
                    return;
                }

                StatusResponse status = response.body().getEstatus();
                if (status.getResultado() == 1) {
                    presenter.onLimpiezaCreada(response.body());
                } else {
                    presenter.onLimpiezaError(status.getMensaje());
                }
            }

            @Override
            public void onFailure(Call<CrearLimpiezaResponse> call, Throwable t) {
                presenter.onLimpiezaError(t.getMessage());
            }
        });
    }
}
