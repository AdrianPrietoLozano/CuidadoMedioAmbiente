package com.example.cuidadodelambiente.ui.fragments.limpiezas;

import com.example.cuidadodelambiente.data.responses.CrearLimpiezaResponse;

public class LimpiezaPresenter implements Contract.Presenter {
    private Contract.View view;
    private Contract.Model model;

    public LimpiezaPresenter(Contract.View view) {
        this.view = view;
        this.model = new LimpiezaModel(this);
    }

    @Override
    public void crearLimpieza(Integer idReporte, String descripcion, String urlFoto) {
        if (view == null) return;

        view.showLoading();
        model.crearLimpieza(idReporte, descripcion, urlFoto);

    }

    @Override
    public void onLimpiezaCreada(CrearLimpiezaResponse response) {
        if (view == null) return;

        view.hideLoading();
        view.showMessage("Limpieza creada correctamente");
        view.onLimpiezaCreada();
        view.close();
    }

    @Override
    public void onLimpiezaError(String error) {
        if (view == null) return;

        view.hideLoading();
        view.showMessage(error);
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
