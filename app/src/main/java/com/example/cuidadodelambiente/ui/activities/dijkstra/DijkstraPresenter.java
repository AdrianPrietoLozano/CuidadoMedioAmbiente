package com.example.cuidadodelambiente.ui.activities.dijkstra;

import com.example.cuidadodelambiente.data.models.UbicacionDijkstra;
import com.example.cuidadodelambiente.data.responses.DijkstraResponse;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DijkstraPresenter implements Contract.Presenter {
    private Contract.View view;
    private Contract.Model model;

    public DijkstraPresenter(Contract.View view) {
        this.view = view;
        this.model = new DijkstraModel(this);
    }

    @Override
    public void fetchDijkstra(LatLng ubicacion, Integer puntos) {
        if (view == null) return;

        if (!view.ubicacionObtenida()) {
            view.showError("Es necesaria la ubicación");
            return;
        }

        view.clearMap();
        view.showLoading();
        model.fetchDijkstra(ubicacion, puntos);
    }

    @Override
    public void onFetchDijkstraExito(List<UbicacionDijkstra> ruta) {
        if (view == null) return;

        view.hideLoading();
        view.showRuta(ruta);
    }

    @Override
    public void onFetchDijkstraError(String error) {
        if (view == null) return;

        view.hideLoading();
        view.showError(error);
    }

    @Override
    public void detachView() {
        view = null;
    }
}
