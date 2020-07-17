package com.example.cuidadodelambiente.ui.fragments.reportes.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.Fragments.CargandoCircular;
import com.example.cuidadodelambiente.ActividadCrearReporte;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.UbicacionReporte;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.ui.fragments.DatosReporteFragment;
import com.example.cuidadodelambiente.ui.fragments.reportes.presenter.IReportesPresenter;
import com.example.cuidadodelambiente.ui.fragments.reportes.presenter.ReportesPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportesContaminacionFragment extends Fragment
        implements IReportesView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{

    private MapView mMapView;
    private GoogleMap mMap;
    private LinearLayout layoutSinConexion;
    private TextView mensajeProblema, totalReportes;
    private Button botonVolverIntentar;
    private FloatingActionButton botonRecargar;
    private FloatingActionButton botonNuevoReporte;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private JsonObjectRequest jsonObjectRequest;
    private CargandoCircular cargandoCircular;
    private IReportesPresenter reportesPresenter;


    public ReportesContaminacionFragment() {
        reportesPresenter = new ReportesPresenter(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_crear_evento, container, false);

        // para la carga circular
        cargandoCircular = new CargandoCircular(v.findViewById(R.id.contenidoPrincipal),
                v.findViewById(R.id.pantallaCarga));
        cargandoCircular.ocultarContenidoMostrarCarga();

        // layout sin conexion
        layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        layoutSinConexion.setVisibility(View.INVISIBLE);

        // mensaje de error que se muestra cuando ocurre algun error
        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        // esta cadena debe cambiarse con datos de la BD
        totalReportes = v.findViewById(R.id.totalReportes);

        // evento clic para el boton volver a intentarlo cuando no hay conexion a internet
        botonVolverIntentar = v.findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        // evento clic para el boton flotante que recarga los marcadores del mapa
        botonRecargar = v.findViewById(R.id.botonFlotanteRecargar);
        botonRecargar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        // evento clic para el boton flotante crear nuevo reporte
        botonNuevoReporte = v.findViewById(R.id.botonFlotanteNuevoReporte);
        botonNuevoReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCrearReporte = new Intent(getActivity(),
                        ActividadCrearReporte.class);
                startActivity(intentCrearReporte);
            }
        });


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = v.findViewById(R.id.mapaRecomendacionEventos);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        intentarPeticionBD();
        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try {
            mMap = map;
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setScrollGesturesEnabled(true);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setTiltGesturesEnabled(true);

            map.moveCamera(CameraUpdateFactory.newCameraPosition(Utilidades.GDL));

            // agregar el evento clic marker al mapa
            map.setOnMarkerClickListener(this);
        }catch(Exception e)
        {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void intentarPeticionBD()
    {
        cargandoCircular.ocultarContenidoMostrarCarga();

        if(Utilidades.hayConexionInternet(getContext())) {
            layoutSinConexion.setVisibility(View.INVISIBLE);

            reportesPresenter.cargarReportes();
        }
        else {
            cargandoCircular.ocultarCargaMostrarContenido();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            layoutSinConexion.setVisibility(View.VISIBLE);
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
    public boolean onMarkerClick(final Marker marker) {
        BottomSheetDialogFragment fragmentDatosReporte = DatosReporteFragment.newInstance((int) marker.getTag());
        fragmentDatosReporte.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        fragmentDatosReporte.show(getFragmentManager(), fragmentDatosReporte.getTag());

        return true;
    }

    @Override
    public void onReportesRecibidosCorrectamente(List<UbicacionReporte> reportes) {
        Log.e("TOTAL", String.valueOf(reportes.size()));

        totalReportes.setText(reportes.size() + " reportes en total");

        for (UbicacionReporte reporte : reportes) {

            Utilidades.agregarMarcadorMapa(mMap,
                    new LatLng(reporte.getLatitud(), reporte.getLongitud()),
                    reporte.getId());
        }
        cargandoCircular.ocultarCargaMostrarContenido();
    }

    @Override
    public void onReportesRecibidosError(Throwable t) {
        cargandoCircular.ocultarCargaMostrarContenido();
        mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
        layoutSinConexion.setVisibility(View.VISIBLE);
    }
}
