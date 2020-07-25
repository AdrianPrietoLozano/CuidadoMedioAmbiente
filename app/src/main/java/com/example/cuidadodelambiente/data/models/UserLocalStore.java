package com.example.cuidadodelambiente.data.models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

import java.util.Observable;

public class UserLocalStore extends Observable {

    public static final String SP_NAME = "usuarioLogueado";
    private static final String ID_STRING = "id";
    private static final String NOMBRE_STRING = "nombre";
    private static final String EMAIL_STRING = "email";
    private static final String PUNTOS_STRING = "puntos";
    private static final String TIPO_USUARIO_STRING = "tipo";
    private static  final String LOGUEADO_STRING = "logueado";

    private SharedPreferences sharedPreferences;
    private static UserLocalStore instance;
    private User usuarioLogueado;

    private UserLocalStore(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
    }

    synchronized public static UserLocalStore getInstance(Context context) {
        if (instance == null) {
            instance = new UserLocalStore(context);
        }

        return instance;
    }

    synchronized public void guardarUsuario(User usuario) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ID_STRING, usuario.getId());
        editor.putString(NOMBRE_STRING, usuario.getNombre());
        editor.putString(EMAIL_STRING, usuario.getEmail());
        editor.putInt(PUNTOS_STRING, usuario.getPuntos());
        editor.putInt(TIPO_USUARIO_STRING, usuario.getTipoUsuario());
        editor.commit();

        usuarioLogueado = usuario;

        setChanged();
        notifyObservers(this.usuarioLogueado);
    }

    synchronized public User getUsuarioLogueado() {
        if (!isUsuarioLogueado())
            return null;

        if (this.usuarioLogueado == null) {
            int id = sharedPreferences.getInt(ID_STRING, -1);
            String nombre = sharedPreferences.getString(NOMBRE_STRING, "");
            String email = sharedPreferences.getString(EMAIL_STRING, "");
            int puntos = sharedPreferences.getInt(PUNTOS_STRING, -1);
            int tipoUsuario = sharedPreferences.getInt(TIPO_USUARIO_STRING, -1);

            this.usuarioLogueado = new User(id, nombre, email, puntos, tipoUsuario);
        }

        return this.usuarioLogueado;
    }

    public void limpiarDatosUsuario() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void setUsuarioLogueado(boolean logueado) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGUEADO_STRING, logueado);
        editor.commit();
    }

    public boolean isUsuarioLogueado() {
        return sharedPreferences.getBoolean(LOGUEADO_STRING, false);
    }


}
