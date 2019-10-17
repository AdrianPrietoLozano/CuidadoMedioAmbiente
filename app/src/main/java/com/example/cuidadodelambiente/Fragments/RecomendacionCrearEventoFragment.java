package com.example.cuidadodelambiente.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cuidadodelambiente.Dialogos.DialogClicReporte;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionCrearEventoFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{

    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";


    public RecomendacionCrearEventoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_crear_evento, container, false);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = v.findViewById(R.id.mapaRecomendacionEventos);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try {
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setScrollGesturesEnabled(true);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setTiltGesturesEnabled(true);

            map.moveCamera(CameraUpdateFactory.newCameraPosition(Utilidades.GDL));

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.671434, -103.350885), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.671273, -103.357194), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.668814, -103.357280), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.666184, -103.356518), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.667991, -103.349909), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.679506, -103.351679), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.672961, -103.346057), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.672720, -103.350509), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.675771, -103.351614), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.670909, -103.353663), "Reporte");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.681975, -103.350638), "Reporte");

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.6734317,-103.3620755), "Guadalajara",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.675771, -103.351614), "Reporte",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.680369, -103.348063),"Reporte",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.672887, -103.353631), "Reporte",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.675978, -103.354478), "Reporte",
                    BitmapDescriptorFactory.HUE_CYAN);

            // agregar el evento clic marker al mapa
            map.setOnMarkerClickListener(this);
        }catch(Exception e)
        {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        DialogFragment dialog = new DialogClicReporte();
        dialog.show(getFragmentManager(), marker.getTitle());

        return true;
    }
}
