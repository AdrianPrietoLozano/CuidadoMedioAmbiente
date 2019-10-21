package com.example.cuidadodelambiente.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public EventosLimpieza() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eventos_limpieza, container, false);

        request = Volley.newRequestQueue(getContext());

        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mMapViewBundle = null;
        if (savedInstanceState != null) {
            mMapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }


        mMapView = v.findViewById(R.id.mapaEventos);
        mMapView.onCreate(mMapViewBundle);
        mMapView.getMapAsync(this);

        // evento clic de las recomendaciones para participar en eventos
        v.findViewById(R.id.layoutEventoParati).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        DeclaracionFragments.recomendacionEventosFragment);
            }
        });

        // evento clic de las recomendaciones para crear un evento
        v.findViewById(R.id.layoutCrearEvento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        DeclaracionFragments.recomendacionCrearEventoFragment);
            }
        });

        // evento clic para el boton flotante que permite crear un nuevo evento
        FloatingActionButton nuevoEvento = v.findViewById(R.id.botonNuevoEvento);
        nuevoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        DeclaracionFragments.crearEventoFragment);
            }
        });


        iniciarPeticionBD();
        return v;
    }

    private void iniciarPeticionBD()
    {
        String url = "http://10.0.0.8/pruebaBDremota/ubicaciones_eventos.php";

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void agregarMarcadores()
    {

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

            /*
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.671434, -103.350885), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.671273, -103.357194), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.668814, -103.357280), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.666184, -103.356518), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.667991, -103.349909), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.679506, -103.351679), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.672961, -103.346057), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.672720, -103.350509), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.675771, -103.351614), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.670909, -103.353663), "Evento");
            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.681975, -103.350638), "Evento");

            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.6734317,-103.3620755), "Guadalajara",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.675771, -103.351614), "Evento",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.680369, -103.348063),"Evento",
                    BitmapDescriptorFactory.HUE_CYAN);

            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.672887, -103.353631), "Evento",
                    BitmapDescriptorFactory.HUE_CYAN);


            Utilidades.agregarMarcadorMapa(mMap, new LatLng(20.675978, -103.354478), "Evento",
                    BitmapDescriptorFactory.HUE_CYAN);*/

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
        Utilidades.iniciarFragment(getFragmentManager().beginTransaction(), new DatosEventoFragment());

        return true;
    }

    // se llama cuando no se logra conectar
    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
        System.out.println();
        Log.i("Error: ", error.toString());
    }

    // se llama cuando se logra conectar
    @Override
    public void onResponse(JSONObject response) {
        progreso.hide();

        JSONArray json = response.optJSONArray("ubicacion");

        JSONObject jsonObject;
        LatLng ubicacion;
        for(int i = 0; i < json.length(); i++)
        {
            try {
                jsonObject = json.getJSONObject(i);

                double lat = jsonObject.optDouble("latitud");
                double lng = jsonObject.optDouble("longitud");
                ubicacion = new LatLng(lat, lng);

                Utilidades.agregarMarcadorMapa(mMap, ubicacion, "Evento");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
