package com.example.cuidadodelambiente.ui.activities.dijkstra;

import com.example.cuidadodelambiente.data.responses.DijkstraResponse;
import com.example.cuidadodelambiente.ui.BasePresenter;
import com.google.android.gms.maps.model.LatLng;

public interface Contract {
    interface View {
        void clearMap();
        void showLoading();
        void hideLoading();
        void showError(String error);
        void showRuta(DijkstraResponse ruta);
        boolean ubicacionObtenida();
    }

    interface Presenter extends BasePresenter {
        void fetchDijkstra(LatLng ubicacion, Integer puntos);
        void onFetchDijkstraExito(DijkstraResponse ruta);
        void onFetchDijkstraError(String error);
    }

    interface Model {
        void fetchDijkstra(LatLng ubicacion, Integer puntos);
    }
}
