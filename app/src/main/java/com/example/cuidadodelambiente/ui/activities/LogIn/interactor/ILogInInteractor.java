package com.example.cuidadodelambiente.ui.activities.LogIn.interactor;

public interface ILogInInteractor {
    public void cargarDatosUsuarioNormal(int idUsuario);
    public void autentificarUsuarioNormal(String email, String contrasenia);
    public void autentificarUsuarioGoogle(String idToken);
}
