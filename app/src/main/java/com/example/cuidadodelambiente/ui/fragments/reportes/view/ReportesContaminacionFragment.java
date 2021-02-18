package com.example.cuidadodelambiente.ui.fragments.reportes.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.MyClusterItem;
import com.example.cuidadodelambiente.MyCustomRenderer;
import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.view.ActividadCrearReporte;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.google.maps.android.clustering.algo.ScreenBasedAlgorithm;
import com.google.maps.android.clustering.algo.ScreenBasedAlgorithmAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportesContaminacionFragment extends Fragment
        implements IReportesView, OnMapReadyCallback,
        Observer
{

    private MapView mMapView;
    private ClusterManager<MyClusterItem> clusterManager;
    private MyCustomRenderer renderer;
    private GoogleMap mMap;
    //private RelativeLayout layoutSinConexion;
    private TextView mensajeProblema, totalReportes;
    private Button botonVolverIntentar;
    private FloatingActionButton botonRecargar;
    private FloatingActionButton botonNuevoReporte;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private JsonObjectRequest jsonObjectRequest;
    private HelperCargaError helperCargaError;
    private IReportesPresenter reportesPresenter;
    private List<UbicacionReporte> reportes;


    public ReportesContaminacionFragment() {
        reportesPresenter = new ReportesPresenter(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_crear_evento, container, false);

        // suscribirse al observable de ActividadCrearReporte
        ActividadCrearReporte.getObservable().addObserver(this);

        // para la carga circular
        helperCargaError = new HelperCargaError(v.findViewById(R.id.contenidoPrincipal),
                v.findViewById(R.id.pantallaCarga), v.findViewById(R.id.layoutSinConexion));
        helperCargaError.mostrarPantallaCarga();

        // layout sin conexion
        //layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        //layoutSinConexion.setVisibility(View.INVISIBLE);

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
        mMapView.getMapAsync(this); //getMapAsync(this);

        intentarPeticionBD();
        return v;
    }

    private void iniciarClusteres() {
        Vector<MyClusterItem> nuevosItems = new Vector<>();

        if(mMap != null) {
            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            for (UbicacionReporte ubicacion : reportes) {
                LatLng p = new LatLng(ubicacion.getLatitud(), ubicacion.getLongitud());
                if (bounds.contains(p)) {
                    MyClusterItem offsetItem = new MyClusterItem(p, ubicacion.getId());
                    nuevosItems.add(offsetItem);
                }
            }

            clusterManager.clearItems();
            clusterManager.addItems(nuevosItems);
            clusterManager.cluster();

        }
    }

    private void intentarPeticionBD()
    {
        helperCargaError.mostrarPantallaCarga();

        if(Utilidades.hayConexionInternet(getContext())) {
            //layoutSinConexion.setVisibility(View.INVISIBLE);
            reportesPresenter.cargarReportes();
        }
        else {
            //helperCargaError.ocultarCargaMostrarContenido();
            helperCargaError.mostrarPantallaError();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            //layoutSinConexion.setVisibility(View.VISIBLE);
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
    public void onReportesRecibidosCorrectamente(List<UbicacionReporte> reportesServer) {
        this.reportes = reportesServer;
        totalReportes.setText(reportes.size() + " reportes en total");

        iniciarClusteres();
        helperCargaError.mostrarContenidoPrincipal();

    }

    @Override
    public void onReportesRecibidosError(Throwable t) {
        //helperCargaError.ocultarContenidoPrincipal();
        mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
        //layoutSinConexion.setVisibility(View.VISIBLE);
        helperCargaError.mostrarPantallaError();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.e("UPDATE", "agregando reporte a mapa");

        // agrega un nuevo marcador al mapa cuando se crea un nuevo reporte
        if (arg instanceof ReporteContaminacion) {
            ReporteContaminacion nuevoReporte = (ReporteContaminacion) arg;

            this.reportes.add(new UbicacionReporte(nuevoReporte.getId(),
                    nuevoReporte.getLatitud(),
                    nuevoReporte.getLongitud()));

            // centra el mapa a la ubicación del nuevo reporte
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(nuevoReporte.getLatitud(), nuevoReporte.getLongitud()))
                            .zoom(16.5f).bearing(0).tilt(25).build()));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Utilidades.GDL));
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            clusterManager = new ClusterManager<>(getActivity(), mMap);
            renderer = new MyCustomRenderer(getActivity(), mMap, clusterManager);
            clusterManager.setRenderer(renderer);

            mMap.setOnCameraIdleListener(clusterManager);
            mMap.setOnMarkerClickListener(clusterManager);


            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    iniciarClusteres();
                }
            });

            clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyClusterItem>() {
                @Override
                public boolean onClusterItemClick(MyClusterItem item) {
                    BottomSheetDialogFragment fragmentDatosReporte = DatosReporteFragment.newInstance(item.getId());
                    fragmentDatosReporte.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                    fragmentDatosReporte.show(getFragmentManager(), fragmentDatosReporte.getTag());

                    return true;
                }
            });

            clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyClusterItem>() {
                @Override
                public boolean onClusterClick(Cluster<MyClusterItem> cluster) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            cluster.getPosition(), (float) Math.floor(mMap.getCameraPosition().zoom + 1)), 300, null
                    );
                    return true;
                }
            });

            mMap.setMyLocationEnabled(true);


        } catch(Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
