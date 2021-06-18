package com.example.cuidadodelambiente.ui.activities.dijkstra;

import com.example.cuidadodelambiente.data.models.UbicacionDijkstra;
import com.example.cuidadodelambiente.data.responses.DijkstraResponse;
import com.example.cuidadodelambiente.ui.BasePresenter;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface Contract {
    interface View {
        void clearMap();
        void showLoading();
        void hideLoading();
        void showError(String error);
        void showRuta(List<UbicacionDijkstra> ruta);
        boolean ubicacionObtenida();
    }

    interface Presenter extends BasePresenter {
        void fetchDijkstra(LatLng ubicacion, Integer puntos);
        void onFetchDijkstraExito(List<UbicacionDijkstra> ruta);
        void onFetchDijkstraError(String error);
    }

    interface Model {
        void fetchDijkstra(LatLng ubicacion, Integer puntos);
    }
}
