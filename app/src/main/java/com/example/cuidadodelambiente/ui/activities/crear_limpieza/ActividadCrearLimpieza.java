package com.example.cuidadodelambiente.ui.activities.crear_limpieza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.ParaObservar;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;

public class ActividadCrearLimpieza extends AppCompatActivity implements Contract.View {

    private final int REQUEST_CODE_ELEGIR_FOTO = 10;

    private int reporteId;
    private LatLng ubicacionReporte;
    private ImageView fotoEvidencia;
    private ImageView iconElegirFoto;
    private Uri uriImagen;
    private Button btnLimpiar;
    private TextInputEditText textDescripcion;
    private ProgressDialog progressDialog;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;
    private static ParaObservar observable = new ParaObservar();
    private Contract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_crear_limpieza);

        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.amarillo2));

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            showMessage("Extras es null");
            finish();
        } else {
            reporteId = extras.getInt(Constants.REPORTE_ID);
            double lat = extras.getDouble(Constants.LATITUD);
            double lon = extras.getDouble(Constants.LONGITUD);
            ubicacionReporte = new LatLng(lat, lon);
        }

        this.presenter = new LimpiezaPresenter(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.amarillo2));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Nueva limpieza");

        CardView cardFoto = findViewById(R.id.cardFoto);
        cardFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Selecciona la fotografía"),
                        REQUEST_CODE_ELEGIR_FOTO);
            }
        });

        fotoEvidencia = findViewById(R.id.fotoEvidencia);
        iconElegirFoto = findViewById(R.id.iconElegirFoto);

        textDescripcion = findViewById(R.id.editTextDescripcion);

        btnLimpiar = findViewById(R.id.limpiar);
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriImagen != null) {
                    String descripcion = textDescripcion.getText().toString();
                    String urlFoto = Utilidades.getRealPathFromURI(uriImagen, getApplicationContext());

                    presenter.crearLimpieza(reporteId, descripcion, urlFoto, ubicacionReporte);
                } else {
                    showMessage("Debes elegir una imagen");
                }
            }
        });
    }

    public static ParaObservar getObservable() {
        return observable;
    }

    @Override
    public void showLoading() {
        progressDialog = new ProgressDialog(ActividadCrearLimpieza.this);
        progressDialog.setTitle("Creando limpieza");
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    @Override
    public void showMessage(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLimpiezaCreada() {
        //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        observable.notificar(null);
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public Activity getActividad() {
        return this;
    }

    @Override
    protected void onDestroy() {
        Log.e("DESTROY", "DESTROY EN LIMPIEZA");
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ELEGIR_FOTO) {
            if (resultCode == Activity.RESULT_OK) {
                uriImagen = data.getData();
                iconElegirFoto.setVisibility(View.GONE);
                fotoEvidencia.setImageURI(uriImagen);
                fotoEvidencia.setVisibility(View.VISIBLE);
            }
        } else {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
