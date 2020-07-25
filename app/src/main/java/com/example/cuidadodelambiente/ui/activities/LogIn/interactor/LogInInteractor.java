package com.example.cuidadodelambiente.ui.activities.LogIn.interactor;

import android.net.DnsResolver;
import android.telecom.RemoteConference;

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

    }

    @Override
    public void autentificarUsuarioNormal(String email, String contrasenia) {

    }

    @Override
    public void autentificarUsuarioGoogle(String idToken) {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.doVerificarGoogleUser(idToken);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    int resultado = json.get("resultado").getAsInt();

                    if (resultado == 1) {
                        int id = json.get("id").getAsInt();
                        String nombre = json.get("nombre").getAsString();
                        String email = json.get("email").getAsString();
                        int puntos = json.get("puntos").getAsInt();

                        User user = new User(id, nombre, email, puntos, User.USUARIO_GOOGLE);
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
}
