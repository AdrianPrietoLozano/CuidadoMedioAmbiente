package com.example.cuidadodelambiente.ui.activities.dijkstra;

import com.google.android.gms.maps.model.LatLng;

public class DijkstraModel implements Contract.Model {
    private Contract.Presenter presenter;

    public DijkstraModel(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void fetchDijkstra(LatLng ubicacion, Integer puntos) {

    }
}
