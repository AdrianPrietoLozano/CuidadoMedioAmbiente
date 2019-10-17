package com.example.cuidadodelambiente.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cuidadodelambiente.DatosEventoFragment;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */


public class EventosLimpieza extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    public EventosLimpieza() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eventos_limpieza, container, false);

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }


        mMapView = v.findViewById(R.id.mapaEventos);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        // evento clic para el boton flotante que permite crear un nuevo evento
        FloatingActionButton nuevoEvento = v.findViewById(R.id.botonNuevoEvento);
        nuevoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        DeclaracionFragments.crearEventoFragment);
            }
        });

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

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.671434, -103.350885), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.671273, -103.357194), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.668814, -103.357280), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.666184, -103.356518), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.667991, -103.349909), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.679506, -103.351679), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.672961, -103.346057), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.672720, -103.350509), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.675771, -103.351614), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.670909, -103.353663), "Evento");
            Utilidades.agregarMarcadorMapa(map, new LatLng(20.681975, -103.350638), "Evento");

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.6734317,-103.3620755), "Guadalajara",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.675771, -103.351614), "Evento",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.680369, -103.348063),"Evento",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(map, new LatLng(20.672887, -103.353631), "Evento",
                    BitmapDescriptorFactory.HUE_CYAN);


            Utilidades.agregarMarcadorMapa(map, new LatLng(20.675978, -103.354478), "Evento",
                    BitmapDescriptorFactory.HUE_CYAN);

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
        Utilidades.iniciarFragment(getFragmentManager().beginTransaction(), new DatosEventoFragment());

        return true;
    }
}
