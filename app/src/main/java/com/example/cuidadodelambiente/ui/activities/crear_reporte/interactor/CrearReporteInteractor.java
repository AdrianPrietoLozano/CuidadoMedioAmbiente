package com.example.cuidadodelambiente.ui.activities.crear_reporte.interactor;

import android.util.Log;
import android.widget.Toast;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.presenter.ICrearReportePresenter;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CrearReporteInteractor implements ICrearReporteInteractor {

    private ICrearReportePresenter presenter;
    private Call<JsonObject> call;

    public CrearReporteInteractor(ICrearReportePresenter presenter) {
        this.presenter = presenter;
    }

    private RequestBody crearTextRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private List<RequestBody> crearListaRequestBody(List<String> lista) {
        List<RequestBody> residuos = new ArrayList<>();
        for(int i = 0; i < lista.size(); i++) {
            RequestBody e = RequestBody.create(MediaType.parse("text"), lista.get(i));
            residuos.add(e);
        }

        return residuos;
    }

    @Override
    public void crearReporte(final ReporteContaminacion reporte, String rutaImagen) {
        File file = new File(rutaImagen);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        int idUsuario = UserLocalStore.getInstance(null).getUsuarioLogueado().getId();

        RequestBody latitud = crearTextRequestBody(String.valueOf(reporte.getLatitud()));
        RequestBody longitud = crearTextRequestBody(String.valueOf(reporte.getLongitud()));
        RequestBody idUsuarioPart = crearTextRequestBody(String.valueOf(idUsuario));
        RequestBody volumen = crearTextRequestBody(reporte.getVolumenResiduo());
        RequestBody descripcion = crearTextRequestBody(reporte.getDescripcion());

        final List<RequestBody> residuos = crearListaRequestBody(reporte.getResiduos());

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        call = service.doAgregarReporte(
                latitud, longitud, residuos, idUsuarioPart, volumen, descripcion, fileToUpload);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    presenter.onReporteCreadoError("No successful");
                    return;
                }

                JsonObject json = response.body();
                if (json.get("resultado").getAsInt() == 1) {
                    String urlImagen = json.get("url_imagen").getAsString();
                    int idReporte = json.get("id_reporte").getAsInt();

                    reporte.setRutaFoto(urlImagen);
                    reporte.setId(idReporte);

                    presenter.onReporteCreadoExitosamente(reporte);

                } else {
                    presenter.onReporteCreadoError(json.get("mensaje").getAsString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (call.isCanceled()) {
                    presenter.onCrearReporteCancelado();
                } else {
                    presenter.onReporteCreadoError(t.getMessage());
                }
            }
        });
    }

    @Override
    public void cancelarCrearReporte() {
        if (call != null) {
            call.cancel();
        }
    }
}
