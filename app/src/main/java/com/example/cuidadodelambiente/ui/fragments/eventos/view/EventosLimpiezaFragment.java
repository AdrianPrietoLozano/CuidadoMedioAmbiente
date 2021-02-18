package com.example.cuidadodelambiente.ui.fragments.eventos.view;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.MyClusterItem;
import com.example.cuidadodelambiente.MyCustomRenderer;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.CercaMedioLejos;
import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.ui.activities.BuscarEventosActivity;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.ui.activities.ActividadCrearEvento;
import com.example.cuidadodelambiente.ui.fragments.DatosReporteFragment;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.DatosEventoFragment;
import com.example.cuidadodelambiente.ui.fragments.eventos.presenter.EventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.eventos.presenter.IEventosPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */


public class EventosLimpiezaFragment extends Fragment
        implements IEventosView, Observer, OnMapReadyCallback {

    private final String TAG = EventosLimpiezaFragment.class.getSimpleName();

    private MapView mMapView;
    private GoogleMap mMap;
    private ClusterManager<MyClusterItem> clusterManager;
    private MyCustomRenderer renderer;
    private List<UbicacionEvento> eventos;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;
    //private LinearLayout layoutSinConexion;
    private FloatingActionButton botonRecargar;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private HelperCargaError helperCargaError;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private IEventosPresenter presenter;

    public EventosLimpiezaFragment() {
        this.presenter = new EventosPresenter(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_eventos_limpieza, container, false);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        Log.e("EVENTOSLIMPIEZA", "onCreateView");

        ActividadCrearEvento.getObservable().addObserver(this);

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mMapViewBundle = null;
        if (savedInstanceState != null) {
            mMapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }


        ImageView iconoBuscar = v.findViewById(R.id.iconoBuscar);
        iconoBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BuscarEventosActivity.class);
                startActivity(intent);
            }
        });

        // para la carga circular
        helperCargaError = new HelperCargaError(v.findViewById(R.id.contenidoPrincipal),
                v.findViewById(R.id.pantallaCarga), v.findViewById(R.id.layoutSinConexion));
        //helperCargaError.ocultarContenidoMostrarCarga();
        helperCargaError.mostrarPantallaCarga();

        //layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        //layoutSinConexion.setVisibility(View.INVISIBLE);

        mMapView = v.findViewById(R.id.mapaEventos);
        mMapView.onCreate(mMapViewBundle);
        mMapView.getMapAsync(this);

        // mensaje de error que se muestra cuando ocurre algun error
        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        // evento clic de las recomendaciones para participar en eventos
        v.findViewById(R.id.layoutEventoParati).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity())
                        .cambiarFragment(DeclaracionFragments.recomendacionEventosFragment, "REEVENTO");
            }
        });

        // evento clic de las recomendaciones para crear un evento
        v.findViewById(R.id.layoutCrearEvento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity())
                        .cambiarFragment(DeclaracionFragments.recomendacionCrearEventoFragment, "RECOMENDACION");
            }
        });

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


        // Bottom Sheet
        bottom_sheet = v.findViewById(R.id.bottom_sheet_opciones);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        intentarPeticionBD();

        return v;
    }

    private void intentarPeticionBD() {
        helperCargaError.mostrarPantallaCarga();

        // si hay conexión a internet
        if (Utilidades.hayConexionInternet(getContext())) {
            //layoutSinConexion.setVisibility(View.INVISIBLE);
            botonRecargar.show();
            presenter.cargarEventos();
            //pruebaFuncion();

        } else { // no hay conexión a internet
            //helperCargaError.ocultarCargaMostrarContenido();
            helperCargaError.mostrarPantallaError();
            botonRecargar.hide();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            //layoutSinConexion.setVisibility(View.VISIBLE);
        }
    }


    //PARA PRUEBAS DE CON ELSA
    private void pruebaFuncion() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetCercaMedioLejos(UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId()).enqueue(new Callback<CercaMedioLejos>() {
            @Override
            public void onResponse(Call<CercaMedioLejos> call, Response<CercaMedioLejos> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "no successful", Toast.LENGTH_SHORT).show();
                    helperCargaError.mostrarPantallaError();
                    return;
                }

                Log.e("TAMANIO", String.valueOf(response.body().getCerca().size() + response.body().getMedio().size() + response.body().getLejos().size()));

                mMap.clear();

                for (UbicacionEvento u : response.body().getCerca()) {
                    Utilidades.agregarMarcadorMapa(mMap, new LatLng(u.getLatitud(), u.getLongitud()), u.getId(), BitmapDescriptorFactory.HUE_GREEN);
                }

                for (UbicacionEvento u : response.body().getMedio()) {
                    Utilidades.agregarMarcadorMapa(mMap, new LatLng(u.getLatitud(), u.getLongitud()), u.getId(), BitmapDescriptorFactory.HUE_YELLOW);
                }

                for (UbicacionEvento u : response.body().getLejos()) {
                    Utilidades.agregarMarcadorMapa(mMap, new LatLng(u.getLatitud(), u.getLongitud()), u.getId(), BitmapDescriptorFactory.HUE_RED);
                }

                helperCargaError.mostrarContenidoPrincipal();

            }

            @Override
            public void onFailure(Call<CercaMedioLejos> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                helperCargaError.mostrarPantallaError();
            }
        });
    }



    private void iniciarClusteres() {
        Vector<MyClusterItem> nuevosItems = new Vector<>();

        if(mMap != null) {
            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            for (UbicacionEvento ubicacion : eventos) {
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Utilidades.GDL));

            // prueba
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    BottomSheetDialogFragment fragmentDatosEvento = DatosEventoFragment.newInstance((Integer) marker.getTag());
                    fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                    fragmentDatosEvento.show(getFragmentManager(), fragmentDatosEvento.getTag());

                    return true;
                }
            });
            /*
            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setScrollGesturesEnabled(true);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setTiltGesturesEnabled(true);
             */


            clusterManager = new ClusterManager<>(getActivity(), mMap);
            renderer = new MyCustomRenderer(getActivity(), mMap, clusterManager);
            clusterManager.setRenderer(renderer);
            clusterManager.setAnimation(false);

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
                    BottomSheetDialogFragment fragmentDatosEvento = DatosEventoFragment.newInstance(item.getId());
                    fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                    fragmentDatosEvento.show(getFragmentManager(), fragmentDatosEvento.getTag());

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

        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mMapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mMapViewBundle == null) {
            mMapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mMapViewBundle);
        }

        mMapView.onSaveInstanceState(mMapViewBundle);

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
        ActividadCrearEvento.getObservable().deleteObserver(this);
        Toast.makeText(getContext(), "Observer eliminado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onEventosCargadosExitosamente(List<UbicacionEvento> eventos) {
        Log.e("TOTAL", String.valueOf(eventos.size()));
        this.eventos = eventos;

        helperCargaError.mostrarContenidoPrincipal();
    }

    @Override
    public void onEventosCargadosError(Throwable t) {
        //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

        //helperCargaError.ocultarCargaMostrarContenido();
        botonRecargar.hide();
        mensajeProblema.setText(R.string.estamos_teniendo_problemas);
        helperCargaError.mostrarPantallaError();
    }

    // se ejecuta cuando se crea un evento de limpieza
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof EventoLimpieza) {
            Log.e(TAG, "UPDATE");

            EventoLimpieza evento = (EventoLimpieza) arg;

            Utilidades.agregarMarcadorMapa(mMap, evento.getUbicacion(), evento.getIdEvento());

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(evento.getUbicacion())
                            .zoom(16.5f).bearing(0).tilt(25).build()));

        }
    }

}
