package com.example.cuidadodelambiente.ui.base;

public interface MvpView {
    boolean isNetworkConnected();
    void showLoading();
    void hideLoading();
    void showError(String error);
    void hideError();
    void showMessage(String msg);
}
