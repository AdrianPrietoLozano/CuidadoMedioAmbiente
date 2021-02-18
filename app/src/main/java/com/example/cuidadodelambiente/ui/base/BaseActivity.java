package com.example.cuidadodelambiente.ui.base;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuidadodelambiente.Utilidades;

public class BaseActivity extends AppCompatActivity implements MvpView {
    @Override
    public boolean isNetworkConnected() {
        return Utilidades.hayConexionInternet(getApplicationContext());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError() {

    }
}
