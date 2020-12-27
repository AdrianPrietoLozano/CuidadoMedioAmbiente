package com.example.cuidadodelambiente.ui.activities.LogIn.presenter;

import com.example.cuidadodelambiente.data.models.User;

public interface ILogInPresenter {
    public void cargarDatosUsuarioNormal(int idUsuario);
    public void cargarDatosUsuarioNormalError(String error);
    public void cargarDatosUsuarioNormalExito(User user);

    public void autentificarUsuarioGoogle(String idToken, String fcmToken);
    public void autentificarUsuarioGoogleError(String error);
    public void autentificarUsuarioGoogleExito(User user);
}
