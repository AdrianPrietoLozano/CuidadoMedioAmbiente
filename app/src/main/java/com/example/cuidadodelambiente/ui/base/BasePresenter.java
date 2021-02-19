package com.example.cuidadodelambiente.ui.base;

public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private V view;

    @Override
    public void attachView(V mvpView) {
        this.view = mvpView;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    public boolean isViewAttached() {
        return this.view != null;
    }

    public V getView() {
        return this.view;
    }
}
