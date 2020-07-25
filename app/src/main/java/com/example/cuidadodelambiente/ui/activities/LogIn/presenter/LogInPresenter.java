package com.example.cuidadodelambiente.ui.activities.LogIn.presenter;

import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.ui.activities.LogIn.interactor.ILogInInteractor;
import com.example.cuidadodelambiente.ui.activities.LogIn.interactor.LogInInteractor;
import com.example.cuidadodelambiente.ui.activities.LogIn.view.ILogInView;

public class LogInPresenter implements ILogInPresenter {

    private ILogInView view;
    private ILogInInteractor interactor;

    public LogInPresenter(ILogInView view) {
        this.view = view;
        interactor = new LogInInteractor(this);
    }

    @Override
    public void cargarDatosUsuarioNormal(int idUsuario) {
        interactor.cargarDatosUsuarioNormal(idUsuario);
    }

    @Override
    public void cargarDatosUsuarioNormalError(String error) {
        view.cargarDatosUsuarioNormalError(error);
    }

    @Override
    public void cargarDatosUsuarioNormalExito(User user) {
        view.cargarDatosUsuarioNormalExito(user);
    }

    @Override
    public void autentificarUsuarioNormal(String email, String contrasenia) {
        interactor.autentificarUsuarioNormal(email, contrasenia);
    }

    @Override
    public void autentificarUsuarioNormalError(String error) {
        view.autentificarUsuarioNormalError(error);
    }

    @Override
    public void autentificarUsuarioNormalExito(User user) {
        view.autentificarUsuarioNormalExito(user);
    }

    @Override
    public void autentificarUsuarioGoogle(String idToken) {
        interactor.autentificarUsuarioGoogle(idToken);
    }

    @Override
    public void autentificarUsuarioGoogleError(String error) {
        view.autentificarUsuarioGoogleError(error);
    }

    @Override
    public void autentificarUsuarioGoogleExito(User user) {
        view.autentificarUsuarioGoogleExito(user);
    }
}
