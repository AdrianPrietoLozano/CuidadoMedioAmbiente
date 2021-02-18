package com.example.cuidadodelambiente.ui.base;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private V view;

    @Override
    public void onAttach(V mvpView) {
        this.view = mvpView;
    }

    @Override
    public void onDetach() {
        this.view = null;
    }

    public V getView() {
        return this.view;
    }
}
