package com.example.cuidadodelambiente.ui.activities.crear_reporte.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.FetchAddressIntentService;
import com.example.cuidadodelambiente.ParaObservar;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.network.ActualizacionesUbicacionHelper;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.presenter.CrearReportePresenter;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.presenter.ICrearReportePresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActividadCrearReporte extends AppCompatActivity implements
    ActualizacionesUbicacionHelper.OnActualizacionUbicacion, ICrearReporteView {

    private final int REQUEST_CODE_ELEGIR_FOTO = 10;
    private final String TAG = ActividadCrearReporte.class.getSimpleName();
    private final String OPCION_POR_DEFECTO_VOLUMEN = "Selecciona una opción";

    private String[] VOLUMENES_RESIDUO = new String[] {"Cabe en una mano",
                                            "Cabe en una mochila",
                                            "Cabe en un automóvil",
                                            "Cabe en un contenedor",
                                            "Cabe en un camión",
                                            "Más grande"};

    private TextInputEditText textDescripcion;
    private AutoCompleteTextView volumenResiduoMenu;
    private ImageView fotoReporte;
    private ImageView iconElegirFoto;
    private TextView textErrorContaminante;
    private TextView fechaReporte;
    private TextView txtDireccionReporte; // direccion completa
    private TextView txtUbicacionReporte; // latitud y longitud
    private ProgressBar progressBarDireccion;
    private Button botonCrearReporte;
    private Button botonCancelar;
    private LinearLayout layoutCheckBox;
    private ChipGroup chipGroupContaminantes;

    private Location mLastLocation;
    private Location mLastLocationAux;

    private AddressResultReceiver resultReceiver; // para obtener la dirección
    private String addressOutput;
    private boolean solicitandoDireccion;
    private ReporteContaminacion reporteContaminacion = new ReporteContaminacion();

    private Uri uriImagen;
    private ActualizacionesUbicacionHelper actualizacionesUbiacion;

    private boolean ubicacionObtenida;
    private boolean direccionObtenida;

    private ProgressDialog progresoCrearReporte;
    private ICrearReportePresenter presenter;

    private static ParaObservar observable = new ParaObservar();

    public ActividadCrearReporte() {
        this.presenter = new CrearReportePresenter(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_crear_reporte);

        // Cambia el color del status bar a verde
        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        CardView cardFoto = findViewById(R.id.cardFoto);
        cardFoto.setOnClickListener(listenerElegirFoto);

        fotoReporte = findViewById(R.id.fotoReporte);
        iconElegirFoto = findViewById(R.id.iconElegirFoto);

        botonCrearReporte = findViewById(R.id.botonAceptar);
        botonCrearReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarCrearReporte();
            }
        });
        botonCancelar = findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });

        textErrorContaminante = findViewById(R.id.errorContaminante);
        txtUbicacionReporte = findViewById(R.id.txtLatitudLongitud);
        txtDireccionReporte = findViewById(R.id.textViewDireccion);
        progressBarDireccion = findViewById(R.id.progressBarDireccion);
        fechaReporte = findViewById(R.id.textViewFecha);
        layoutCheckBox = findViewById(R.id.layoutCheckBox);

        chipGroupContaminantes = findViewById(R.id.chipGroupContaminantes);

        textDescripcion = findViewById(R.id.editTextDescripcion);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.dropdown_menu_popup_item,
                VOLUMENES_RESIDUO);
        volumenResiduoMenu = findViewById(R.id.volumen_residuo);
        volumenResiduoMenu.setAdapter(adapter);
        volumenResiduoMenu.setText(OPCION_POR_DEFECTO_VOLUMEN, false);
        volumenResiduoMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                volumenResiduoMenu.setError(null);
            }
        });


        mostrarFechaHora();
        resultReceiver = new AddressResultReceiver(new Handler());

        // iniciando acceder a la ubicación del usuario
        actualizacionesUbiacion = new ActualizacionesUbicacionHelper(this, this);
        actualizacionesUbiacion.iniciarObtenerUbicacion();

        solicitandoDireccion = true;
        ubicacionObtenida = false;
        direccionObtenida = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocationAux = location;
        startIntentService();
    }

    @Override
    public void onConnectionFailed() {
        Toast.makeText(getApplicationContext(), "ConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        finish();
    }


    // Para obtener la dirección dado una latitud y longitud
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // mostrar ubicación (latitud y longitud)
            mLastLocation = mLastLocationAux;
            mostrarUbicacionEnTextView();
            ubicacionObtenida = true;


            if (null == resultData || resultCode == Constants.FAILURE_RESULT ||
                    null == resultData.getString(Constants.RESULT_DATA_KEY)) {
                // si la dirección no había sido obtenida anteriormente
                // se muestra un mensaje de error
                if (!direccionObtenida) {
                    txtDireccionReporte.setText("Ocurrió un error. Reintentando...");
                }
                return;
            }

            // dirección obtenida con éxito
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            mostrarDireccionEnTextView();
            direccionObtenida = true;
            solicitandoDireccion = false;

            actualizarUI();
        }

    }

    public static ParaObservar getObservable() {
        return observable;
    }

    private void mostrarDireccionEnTextView() {
        if (addressOutput != null) {
            Log.e(TAG, "mostrando direccion");
            txtDireccionReporte.setText(addressOutput);
        }
    }

    private void mostrarUbicacionEnTextView() {
        if (mLastLocation != null) {
            Log.e(TAG, "mostrando ubicacion");
            txtUbicacionReporte.setText(String.format("%f, %f", mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    // inicia obtener dirección dado una latitud y longitud
    protected void startIntentService() {
        solicitandoDireccion = true;
        actualizarUI();

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(mLastLocationAux.getLatitude());
        location.setLongitude(mLastLocationAux.getLongitude());
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private void actualizarUI() {
        if (solicitandoDireccion) {
            Log.e(TAG, "progress bar visible");
            progressBarDireccion.setVisibility(View.VISIBLE);

            if (!ubicacionObtenida)
                txtDireccionReporte.setText("Obteniendo dirección ...");
        } else {
            progressBarDireccion.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        actualizacionesUbiacion.detenerActualizacionesUbicacion();
    }

    @Override
    protected void onStop() {
        super.onStop();

        actualizacionesUbiacion.desconectar();
    }

    @Override
    public void onResume(){
        super.onResume();

        actualizacionesUbiacion.iniciarActualizacionesUbicacion();
    }

    @Override
    protected void onStart() {
        super.onStart();

        actualizacionesUbiacion.conectar();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        actualizacionesUbiacion.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void mostrarFechaHora() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy",
                Constants.LOCALE_MX);

        fechaReporte.setText(dateFormat.format(new Date()));

    }

    private View.OnClickListener listenerElegirFoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent, "Selecciona la fotografía"),
                    REQUEST_CODE_ELEGIR_FOTO);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if ( requestCode == REQUEST_CODE_ELEGIR_FOTO ) {
           if (resultCode == RESULT_OK) {
               uriImagen = data.getData();
               iconElegirFoto.setVisibility(View.GONE);
               fotoReporte.setImageURI(uriImagen);
               fotoReporte.setVisibility(View.VISIBLE);
           } else {
               Toast.makeText(getApplicationContext(), "Error al cargar foto", Toast.LENGTH_SHORT).show();
           }
       } else {
           actualizacionesUbiacion.onActivityResult(requestCode, resultCode, data);
       }
    }

    private void cancelar() {
        super.onBackPressed();
    }

    private void iniciarCrearReporte() {
        textErrorContaminante.setVisibility(View.GONE);

        // fecha y hora
        SimpleDateFormat sFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String fecha = sFecha.format(new Date());
        String hora = sHora.format(new Date());
        reporteContaminacion.setFecha(fecha);
        reporteContaminacion.setHora(hora);
        Log.e(TAG, reporteContaminacion.getFecha() + ", " + reporteContaminacion.getHora());

        // foto
        if (uriImagen == null) {
            Toast.makeText(getApplicationContext(), "Debes elegir una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // ubicación
        if (ubicacionObtenida) {
            reporteContaminacion.setLatitud(mLastLocation.getLatitude());
            reporteContaminacion.setLongitud(mLastLocation.getLongitude());
            Log.e(TAG, reporteContaminacion.getLatitud() + ", " + reporteContaminacion.getLongitud());
        } else {
            Log.e(TAG, "Aún no se obtiene la ubicación");
            return;
        }

        // volumen
        String volumen = volumenResiduoMenu.getText().toString();
        if (volumen.equals(OPCION_POR_DEFECTO_VOLUMEN)) {
            volumenResiduoMenu.setError("Debes elegir una opcion");
            return;
        }
        reporteContaminacion.setVolumenResiduo(volumen);
        Log.e(TAG, reporteContaminacion.getVolumenResiduo());

        // contaminantes o residuos
        /*
        List<String> contaminantes = obtenerContaminantes();
        if (contaminantes.isEmpty()) {
            textErrorContaminante.setVisibility(View.VISIBLE);
            return;
        }
        reporteContaminacion.setResiduos(contaminantes);
        Log.e(TAG, reporteContaminacion.getResiduos().toString());
         */

        List<String> contaminantes = obtenerContaminantes();
        if (contaminantes.isEmpty()) {
            textErrorContaminante.setVisibility(View.VISIBLE);
            return;
        }
        reporteContaminacion.setResiduos(contaminantes);
        Log.e(TAG, reporteContaminacion.getResiduos().toString());

        // descripción
        String descripcion = textDescripcion.getText().toString();
        if (descripcion.equals("")) {
            textDescripcion.setError("Campo obligatorio");
            return;
        }
        reporteContaminacion.setDescripcion(descripcion);
        Log.e(TAG, reporteContaminacion.getDescripcion());

        presenter.crearReporte(reporteContaminacion,
                Utilidades.getRealPathFromURI(uriImagen, getApplicationContext()));

    }

    // retorna una lista con los contaminantes que el usuario seleccionó
    private List<String> obtenerContaminantes() {
        /*
        List<String> contaminantes = new ArrayList<>();
        for(int i = 0; i < layoutCheckBox.getChildCount(); i++) {
            if (layoutCheckBox.getChildAt(i) instanceof  LinearLayout) {
                LinearLayout layout = (LinearLayout) layoutCheckBox.getChildAt(i);
                for (int j = 0; j < layout.getChildCount(); j++) {
                    if (layout.getChildAt(j) instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox)layout.getChildAt(j);
                        if (checkBox.isChecked()) {
                            contaminantes.add(checkBox.getText().toString());
                        }
                    }
                }
            }
        }

        return contaminantes;
        */

        List<String> contaminantes = new ArrayList<>();
        List<Integer> ids = chipGroupContaminantes.getCheckedChipIds();
        for (Integer id : ids) {
            Chip chip = chipGroupContaminantes.findViewById(id);
            contaminantes.add(chip.getText().toString());
        }

        return contaminantes;

    }

    @Override
    public void onReporteCreadoExitosamente(ReporteContaminacion reporte) {
        Log.e(TAG, "onReporteCreadoExitosamente");

        Toast.makeText(getApplicationContext(), "Reporte creado exitosamente.", Toast.LENGTH_LONG).show();

        getObservable().notificar(reporte);
        finish();
    }

    @Override
    public void onReporteCreadoError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

        actualizacionesUbiacion.iniciarActualizacionesUbicacion();
    }

    @Override
    public void mostrarDialogoCarga() {
        progresoCrearReporte = new ProgressDialog(ActividadCrearReporte.this);
        progresoCrearReporte.setTitle("Creando reporte");
        progresoCrearReporte.setMessage("Cargando");
        progresoCrearReporte.setCancelable(false);
        progresoCrearReporte.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.cancelarCrearReporte();
            }
        });

        progresoCrearReporte.show();
    }

    @Override
    public void cerrarDialgoCarga() {
        if (progresoCrearReporte.isShowing()) {
            progresoCrearReporte.dismiss();
        }
    }

}
