package com.example.cuidadodelambiente.ui.activities.crear_reporte.view;


import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.network.ActualizacionesUbicacionHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class ElegirUbicacionDialogFragment extends DialogFragment
    implements OnMapReadyCallback, ActualizacionesUbicacionHelper.OnActualizacionUbicacion {

    private static final String ARG_LATITUD = "latitud";
    private static final String ARG_LONGITUD = "longitud";
    public static final String TAG = ElegirUbicacionDialogFragment.class.getSimpleName();

    private GoogleMap map;

    private LatLng ubicacionMarker;
    private MarkerOptions markerOptions;


    public ElegirUbicacionDialogFragment() {
        // Required empty public constructor
    }

    public static ElegirUbicacionDialogFragment newInstance(Double latitud, Double longitud) {
        ElegirUbicacionDialogFragment fragment = new ElegirUbicacionDialogFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUD, latitud);
        args.putDouble(ARG_LONGITUD, longitud);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        double latitud = 0.0;
        double longitud = 0.0;

        if (savedInstanceState != null) {
            Log.e("CREATE", "onCreate savedInstances");
            latitud = savedInstanceState.getDouble(ARG_LATITUD, 0.0);
            longitud = savedInstanceState.getDouble(ARG_LONGITUD, 0.0);
            Log.e("CREATE", String.valueOf(latitud));
            Log.e("CREATE", String.valueOf(longitud));

        } else if (getArguments() != null) {
            latitud = getArguments().getDouble(ARG_LATITUD, 0.0);
            longitud = getArguments().getDouble(ARG_LONGITUD, 0.0);
        }

        if (latitud != 0.0 && longitud != 0.0) {
            ubicacionMarker = new LatLng(latitud, longitud);
        } else {
            ubicacionMarker = Utilidades.DEFAULT_POSITION_MAP;
        }

        markerOptions = new MarkerOptions()
                .position(ubicacionMarker)
                .draggable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_elegir_ubicacion_dialog, container, false);


        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }



        Button btnEligirUbicacion = v.findViewById(R.id.btnElegirUbicacion);
        btnEligirUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbicacionElegidaListener listener = (UbicacionElegidaListener) getActivity();
                if (listener != null) {
                    listener.onUbicacionElegida(ubicacionMarker);
                }

                dismiss();
            }
        });


        return v;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e("onSave", "OnSave savedInstances");
        outState.putDouble(ARG_LATITUD, ubicacionMarker.latitude);
        outState.putDouble(ARG_LONGITUD, ubicacionMarker.longitude);
        Log.e("onSave", String.valueOf(ubicacionMarker.latitude));
        Log.e("onSave", String.valueOf(ubicacionMarker.longitude));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.mapa);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        try {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.addMarker(markerOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15));

            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    ubicacionMarker = marker.getPosition();
                }
            });

            map.setMyLocationEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed() {

    }

    @Override
    public void onPermissionError(String error) {

    }

    @Override
    public void onError(String error) {

    }


    public interface UbicacionElegidaListener {
        void onUbicacionElegida(LatLng ubicacion);
    }
}
