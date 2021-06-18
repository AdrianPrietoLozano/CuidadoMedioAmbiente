package com.example.cuidadodelambiente.ui.activities.dijkstra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.network.ActualizacionesUbicacionHelper;
import com.example.cuidadodelambiente.data.responses.DijkstraResponse;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.DatosEventoFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class DijkstraActivity extends AppCompatActivity
        implements Contract.View, OnMapReadyCallback, ActualizacionesUbicacionHelper.OnActualizacionUbicacion {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;
    private MapView mMapView;
    private GoogleMap mMap;
    private EditText puntosTxt;
    private ProgressBar loading;
    private Button botonAceptar;
    private Contract.Presenter presenter;
    private ActualizacionesUbicacionHelper actualizacionesUbicacion;
    private Location userLocation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dijkstra);

        presenter = new DijkstraPresenter(this);

        // Cambia el color del status bar a verde
        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Dijkstra");

        puntosTxt = findViewById(R.id.puntos);
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        botonAceptar = findViewById(R.id.btnAceptar);
        botonAceptar.setEnabled(false);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!puntosTxt.getText().toString().equals("")) {
                    peticionBD();
                }
            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = findViewById(R.id.mapa);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        actualizacionesUbicacion = new ActualizacionesUbicacionHelper(this, this, 60 * (60 * 1000));
        actualizacionesUbicacion.iniciarObtenerUbicacion();
    }

    private void peticionBD() {
        Integer puntos = Integer.valueOf(puntosTxt.getText().toString());
        if (puntos <= 0) return;

        presenter.fetchDijkstra(null, puntos);
    }

    @Override
    public void clearMap() {
        mMap.clear();
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRuta(DijkstraResponse ruta) {


    }

    @Override
    public boolean ubicacionObtenida() {
        return userLocation != null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<LatLng> ubicaciones = new ArrayList<>();
        ubicaciones.add(new LatLng(20.676702, -103.346283));
        ubicaciones.add(new LatLng(20.681585, -103.346786));
        ubicaciones.add(new LatLng(20.685005, -103.343102));
        ubicaciones.add(new LatLng(20.688068, -103.342316));
        ubicaciones.add(new LatLng(20.691410, -103.340706));
        ubicaciones.add(new LatLng(20.695995, -103.342796));

        try {
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Utilidades.GDL));
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Integer id = Integer.valueOf(marker.getTag().toString());
                    BottomSheetDialogFragment fragmentDatosEvento = DatosEventoFragment.newInstance(id);
                    fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                    fragmentDatosEvento.show(getSupportFragmentManager(), fragmentDatosEvento.getTag());
                    return true;
                }
            });

            //Utilidades.agregarMarcadorMapa(mMap, new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 1, BitmapDescriptorFactory.HUE_GREEN);
            for (LatLng u : ubicaciones) {
                Utilidades.agregarMarcadorMapa(mMap, u, 1);
            }

            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .color(getApplicationContext().getResources().getColor(R.color.colorPrimary))
                    .addAll(ubicaciones));

        } catch (Exception e) {}
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
    public void onLocationChanged(Location location) {
        userLocation = location;
        botonAceptar.setEnabled(true);
    }

    @Override
    public void onConnectionFailed() {
        showError("Falló la conexión. Inténtalo de nuevo");
        botonAceptar.setEnabled(false);
    }

    @Override
    public void onPermissionError(String error) {
        showError(error);
        finish();
    }

    @Override
    public void onError(String error) {
        showError(error);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        actualizacionesUbicacion.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        actualizacionesUbicacion.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
