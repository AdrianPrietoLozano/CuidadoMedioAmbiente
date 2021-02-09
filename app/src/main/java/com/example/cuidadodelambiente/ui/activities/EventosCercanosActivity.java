package com.example.cuidadodelambiente.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventosCercanosActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;
    private MapView mMapView;
    private GoogleMap mMap;
    private EditText kilometros;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos_cercanos);

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
        toolbarTitle.setText("Eventos cercanos");

        kilometros = findViewById(R.id.kilometros);
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        findViewById(R.id.btnAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!kilometros.getText().toString().equals("")) {
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
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Utilidades.GDL));
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } catch (Exception e) {}
    }

    private void peticionBD() {
        loading.setVisibility(View.VISIBLE);
        float radio = Float.parseFloat(kilometros.getText().toString());
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventosCercanos(20.676283, -103.351913, radio).enqueue(new Callback<List<UbicacionEvento>>() {
            @Override
            public void onResponse(Call<List<UbicacionEvento>> call, Response<List<UbicacionEvento>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(EventosCercanosActivity.this, "no successful", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    return;
                }

                mMap.clear();
                for (UbicacionEvento e : response.body()) {
                    Utilidades.agregarMarcadorMapa(mMap, new LatLng(e.getLatitud(), e.getLongitud()), e.getId());
                }

                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(20.676283, -103.351913))
                        .radius(radio*1000)
                        .strokeWidth(3)
                        .strokeColor(Color.parseColor("#00a65d"))
                        .fillColor(Color.parseColor("#4000a65d")));


                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<UbicacionEvento>> call, Throwable t) {
                Toast.makeText(EventosCercanosActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }
}
