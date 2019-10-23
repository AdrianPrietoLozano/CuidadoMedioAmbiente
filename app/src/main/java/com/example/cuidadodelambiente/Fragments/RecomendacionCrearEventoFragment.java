package com.example.cuidadodelambiente.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionCrearEventoFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        Response.Listener<JSONObject>, Response.ErrorListener
{

    private MapView mMapView;
    private GoogleMap mMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    public RecomendacionCrearEventoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_crear_evento, container, false);

        request = Volley.newRequestQueue(getContext());

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = v.findViewById(R.id.mapaRecomendacionEventos);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        iniciarPeticionBD();
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

    private void iniciarPeticionBD()
    {
        String url = "http://192.168.1.68/EventosLimpieza/ubicaciones_reportes.php";

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
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
        DialogFragment dialog = DialogClicReporte.newInstance((int)marker.getTag());
        dialog.show(getFragmentManager(), "TAG");

        return true;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
        System.out.println();
        Log.i("Error: ", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {

        progreso.hide();

        JSONArray json = response.optJSONArray("reportes");

        JSONObject jsonObject;
        LatLng ubicacion;
        for(int i = 0; i < json.length(); i++)
        {
            try {
                jsonObject = json.getJSONObject(i);

                double lat = jsonObject.optDouble("latitud");
                double lng = jsonObject.optDouble("longitud");
                int id_reporte = jsonObject.optInt("id_reporte");
                ubicacion = new LatLng(lat, lng);

                Utilidades.agregarMarcadorMapa(mMap, ubicacion, id_reporte);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
