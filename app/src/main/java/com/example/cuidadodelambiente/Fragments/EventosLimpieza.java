package com.example.cuidadodelambiente.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */


public class EventosLimpieza extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        Response.Listener<JSONObject>, Response.ErrorListener {

    private MapView mMapView;
    private GoogleMap mMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;
    private LinearLayout layoutSinConexion;
    private FloatingActionButton botonNuevoEvento;
    private FloatingActionButton botonRecargar;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private CargandoCircular cargandoCircular;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;

    public EventosLimpieza() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_eventos_limpieza, container, false);

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mMapViewBundle = null;
        if (savedInstanceState != null) {
            mMapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }


        // para la carga circular
        cargandoCircular = new CargandoCircular(v.findViewById(R.id.contenido),
                v.findViewById(R.id.pantallaCarga));
        cargandoCircular.ocultarContenidoMostrarCarga();

        layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        layoutSinConexion.setVisibility(View.INVISIBLE);

        mMapView = v.findViewById(R.id.mapaEventos);
        mMapView.onCreate(mMapViewBundle);
        mMapView.getMapAsync(this);

        // mensaje de error que se muestra cuando ocurre algun error
        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        // evento clic de las recomendaciones para participar en eventos
        v.findViewById(R.id.layoutEventoParati).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        DeclaracionFragments.recomendacionEventosFragment, "REEVENTO");
            }
        });

        // evento clic de las recomendaciones para crear un evento
        v.findViewById(R.id.layoutCrearEvento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        DeclaracionFragments.recomendacionCrearEventoFragment, "RECOMENDACION");
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

        // evento clic para el boton flotante que permite crear un nuevo evento
        botonNuevoEvento = v.findViewById(R.id.botonNuevoEvento);
        botonNuevoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        DeclaracionFragments.crearEventoFragment, "CREAR");
            }
        });

        // evento clic para el boton flotante que recarga los marcadores del mapa
        botonRecargar = v.findViewById(R.id.botonFlotanteRecargar);
        botonRecargar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DeclaracionFragments.actualAmbientalista += 1;
                Toast.makeText(getContext(), "Ambientalista: " +
                        DeclaracionFragments.actualAmbientalista, Toast.LENGTH_SHORT).show();
                intentarPeticionBD();
            }
        });


        // Bottom Sheet
        bottom_sheet = v.findViewById(R.id.bottom_sheet_opciones);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        intentarPeticionBD();
        return v;
    }



    private void intentarPeticionBD()
    {
        cargandoCircular.ocultarContenidoMostrarCarga();

        // si hay conexión a internet
        if(Utilidades.hayConexionInternet(getContext())) {
            layoutSinConexion.setVisibility(View.INVISIBLE);
            botonNuevoEvento.show();
            botonRecargar.show();
            iniciarPeticionBD();
        }
        else { // no hay conexión a internet
            cargandoCircular.ocultarCargaMostrarContenido();
            botonNuevoEvento.hide();
            botonRecargar.hide();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            layoutSinConexion.setVisibility(View.VISIBLE);
        }
    }

    public void recargar()
    {
        intentarPeticionBD();
    }

    public void moverMapa(LatLng ubicacion)
    {
        CameraUpdate current = CameraUpdateFactory.newLatLngZoom(ubicacion, 15);
        mMap.moveCamera(current);
    }

    private void iniciarPeticionBD()
    {
        String url = getString(R.string.ip) + "EventosLimpieza/ubicaciones_eventos.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setScrollGesturesEnabled(true);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setTiltGesturesEnabled(true);

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Utilidades.GDL));
            mMap.setOnMarkerClickListener(this);

        }catch(Exception e)
        {
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Fragment fragmentDatosEvento = DatosEventoFragment.newInstance((int)marker.getTag());
        Utilidades.iniciarFragment(getFragmentManager().beginTransaction(), fragmentDatosEvento, "DATOS");

        return true;
    }

    // se llama cuando no se logra conectar
    @Override
    public void onErrorResponse(VolleyError error) {
        cargandoCircular.ocultarCargaMostrarContenido();
        botonNuevoEvento.hide();
        botonRecargar.hide();
        mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
        layoutSinConexion.setVisibility(View.VISIBLE);
    }

    // se llama cuando se logra conectar
    @Override
    public void onResponse(JSONObject response) {
        JSONArray json = response.optJSONArray("ubicacion");

        JSONObject jsonObject;
        LatLng ubicacion;

        try
        {
            for(int i = 0; i < json.length(); i++)
            {
                jsonObject = json.getJSONObject(i);

                int id_evento = jsonObject.optInt("id_evento");
                int eventoRecomendado = jsonObject.optInt("recomendado");

                double lat = jsonObject.optDouble("latitud");
                double lng = jsonObject.optDouble("longitud");
                ubicacion = new LatLng(lat, lng);

                if(eventoRecomendado == 1) {
                    Utilidades.agregarMarcadorMapa(mMap, ubicacion, id_evento, BitmapDescriptorFactory.HUE_CYAN);
                }
                else{
                    Utilidades.agregarMarcadorMapa(mMap, ubicacion, id_evento);
                }
            }

            cargandoCircular.ocultarCargaMostrarContenido();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
