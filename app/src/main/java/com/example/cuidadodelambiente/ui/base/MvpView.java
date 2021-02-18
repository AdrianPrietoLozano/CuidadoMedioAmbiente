package com.example.cuidadodelambiente.ui.base;

public interface MvpView {
    boolean isNetworkConnected();
    void showLoading();
    void showError();
}
