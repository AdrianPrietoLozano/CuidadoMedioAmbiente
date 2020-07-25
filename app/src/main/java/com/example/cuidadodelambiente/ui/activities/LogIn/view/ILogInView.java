package com.example.cuidadodelambiente.ui.activities.LogIn.view;

import com.example.cuidadodelambiente.data.models.User;

public interface ILogInView {

    public void cargarDatosUsuarioNormalError(String error);
    public void cargarDatosUsuarioNormalExito(User user);

    public void autentificarUsuarioNormalError(String error);
    public void autentificarUsuarioNormalExito(User user);

    public void autentificarUsuarioGoogleError(String error);
    public void autentificarUsuarioGoogleExito(User user);
}
