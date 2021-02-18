package com.example.cuidadodelambiente.ui.activities.LogIn.interactor;

import android.util.Log;

import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.activities.LogIn.presenter.ILogInPresenter;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInInteractor implements ILogInInteractor {

    private ILogInPresenter presenter;

    public LogInInteractor(ILogInPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void cargarDatosUsuarioNormal(int idUsuario) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.doCargarDatosUsuario(idUsuario);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    int resultado = json.get("resultado").getAsInt();

                    if (resultado == 1) {
                        User user = obtenerUsuario(json, User.USUARIO_NORMAL);
                        Log.e("INTERACTOR", "datos recibidos INTERACTOR");
                        presenter.cargarDatosUsuarioNormalExito(user);

                    } else {
                        presenter.cargarDatosUsuarioNormalError(json.get("mensaje").getAsString());
                    }

                } else {
                    presenter.cargarDatosUsuarioNormalError("no successful");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                presenter.cargarDatosUsuarioNormalError(t.getMessage());
                Log.e("INTERACTOR", "error interactor");
            }
        });
    }

    @Override
    public void autentificarUsuarioGoogle(String idToken, String fcmToken) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.doVerificarGoogleUser(idToken, fcmToken);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    int resultado = json.get("resultado").getAsInt();

                    if (resultado == 1) {
                        User user = obtenerUsuario(json, User.USUARIO_GOOGLE);
                        presenter.autentificarUsuarioGoogleExito(user);

                    } else {
                        presenter.autentificarUsuarioGoogleError(json.get("mensaje").getAsString());
                    }

                } else {
                    presenter.autentificarUsuarioGoogleError("no successful");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                presenter.autentificarUsuarioGoogleError(t.getMessage());
            }
        });

    }


    private User obtenerUsuario(JsonObject json, int tipoUsuario) {
        int id = json.get("id").getAsInt();
        String nombre = json.get("nombre").getAsString();
        String email = json.get("email").getAsString();
        int puntos = json.get("puntos").getAsInt();
        String token = json.has("token") ? json.get("token").getAsString() : "";

        return new User(id, token, nombre, email, puntos, tipoUsuario);
    }

}
