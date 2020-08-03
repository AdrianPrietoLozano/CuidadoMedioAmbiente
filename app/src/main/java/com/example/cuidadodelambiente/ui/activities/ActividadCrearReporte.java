package com.example.cuidadodelambiente.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.FetchAddressIntentService;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActividadCrearReporte extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final int REQUEST_CODE_ELEGIR_FOTO = 10;
    private final int PERMISSION_ID = 40;
    private final int REQUEST_CHECK_SETTINGS = 50;
    private final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private final int UPDATE_INTERVAL_MILLISECONDS = 20000; // 20 segundos
    private final int FASTEST_UPDATE_INTERVAL_MILLISECONDS =
            UPDATE_INTERVAL_MILLISECONDS / 2;
    private final String TAG = ActividadCrearReporte.class.getSimpleName();
    private final String OPCION_POR_DEFECTO_VOLUMEN = "Selecciona una opción";

    String[] VOLUMENES_RESIDUO = new String[] {"Cabe en una mano",
                                            "Cabe en una mochila",
                                            "Cabe en un automóvil",
                                            "Cabe en un contenedor",
                                            "Cabe en un camión",
                                            "Más grande"};

    private TextInputEditText textDescripcion;
    private AutoCompleteTextView volumenResiduoMenu;
    private ImageView fotoReporte;
    private MaterialButton botonElegirFoto;
    private TextView textErrorContaminante;
    private TextView fechaReporte;
    private TextView txtDireccionReporte; // direccion completa
    private TextView txtUbicacionReporte; // latitud y longitud
    private ProgressBar progressBarDireccion;
    private Button botonCrearReporte;
    private Button botonCancelar;
    private LinearLayout layoutCheckBox;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mLastLocationAux;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingRequest;

    private AddressResultReceiver resultReceiver;
    private String addressOutput;
    private boolean solicitandoDireccion;

    private Uri uriImagen;

    private boolean ubicacionObtenida;
    private boolean direccionObtenida;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_crear_reporte);


        // Cambia el color del status bar a verde
        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        fotoReporte = findViewById(R.id.fotoReporte);
        fotoReporte.setOnClickListener(listenerElegirFoto);
        botonElegirFoto = findViewById(R.id.btnElegirFoto);
        botonElegirFoto.setOnClickListener(listenerElegirFoto);
        botonCrearReporte = findViewById(R.id.botonAceptar);
        botonCrearReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarCrearEvento();
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

        Log.e(TAG, volumenResiduoMenu.getText().toString());


        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingRequest();

        mostrarFechaHora();
        resultReceiver = new AddressResultReceiver(new Handler());
        solicitandoDireccion = true;
        ubicacionObtenida = false;
        direccionObtenida = false;

        iniciarRequestUbicacion();

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


    private void iniciarRequestUbicacion() {
        if (checkPermissions()) {
            intentarConectar();
            if (isLocationEnabled()) {
                if (checkGooglePlayServices()) {

                    // preparar solicitud de conexión
                    mGoogleApiClient.connect();
                }
            }
            else {
                checkLocationSettings();
            }
        } else {
            solicitarPermisoUbicacion();
        }
    }

    private boolean checkGooglePlayServices() {
        int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(checkGooglePlayServices != ConnectionResult.SUCCESS) {
            /*

            google play services is missing or update is required
            return code could be:
            SUCCESS,
            SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
            SERVICE_DISABLED, SERVICE_INVALID
             */
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        mLocationSettingRequest = builder.build();

    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingRequest
                );
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // todos los permisos son satisfechos
                        Log.e("SUCCESS", "Todos los permisos satisfechos");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("RESOLUTION", "MOSTRAR_DIALOGO");
                        try {
                            status.startResolutionForResult(ActividadCrearReporte.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("CATCH", "No se pudo crear el dialogo");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("UNAVAILABLE", "Inaccesibles");
                        finish();
                        break;
                }
            }
        });
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                // puede servir de algo
            }
        });
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocationAux = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocationAux != null) {
            startIntentService(); // intentar obtener la dirección del usuario
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "onConnectionSuespended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "OnConnectionFailed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocationAux = location;

        // intentar obtener la dirección del usuario
        startIntentService();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(checkPermissions()) {
            intentarConectar();
        }
    }

    private void intentarConectar(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    // pasar contexto a Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }


    // revisa si se tienen los permisos para acceder a la ubicación del usuario
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // ver si esto es necesario
                //intentarConectar();

                iniciarRequestUbicacion();
            } else {
                finish();
            }
        }
    }


    // pide permisos al usuario para acceder a su ubicación
    private void solicitarPermisoUbicacion(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    // revisa si la ubicación esta activada
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void mostrarFechaHora() {
        Locale espaniolLocale = new Locale("es", "ES");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", espaniolLocale);

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

        switch (requestCode) {
            case REQUEST_CODE_ELEGIR_FOTO:
                if (resultCode == RESULT_OK) {
                    uriImagen = data.getData();
                    fotoReporte.setImageURI(uriImagen);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al cargar foto", Toast.LENGTH_SHORT).show();
                }

                break;


            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK) {
                    //iniciarRequestUbicacion();
                    startLocationUpdates();
                }
                else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Permiso necesario", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;


            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                } else if (requestCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Google Play Services es necesario", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void cancelar() {
        super.onBackPressed();
    }

    private void iniciarCrearEvento() {
        textErrorContaminante.setVisibility(View.GONE);

        // fecha y hora
        SimpleDateFormat sFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String fecha = sFecha.format(new Date());
        String hora = sHora.format(new Date());
        Log.e(TAG, fecha + ", " + hora);

        // ubicación
        double latitud = mLastLocation.getLatitude();
        double longitud = mLastLocation.getLongitude();
        Log.e(TAG, latitud + ", " + longitud);

        // volumen
        String volumen = volumenResiduoMenu.getText().toString();
        if (volumen.equals(OPCION_POR_DEFECTO_VOLUMEN)) {
            volumenResiduoMenu.setError("Debes elegir una opcion");
            return;
        }
        Log.e(TAG, volumen);

        // contaminante o tipo residuo
        List<String> contaminantes = obtenerContaminantes();
        if (contaminantes.isEmpty()) {
            textErrorContaminante.setVisibility(View.VISIBLE);
            return;
        }
        Log.e(TAG, contaminantes.toString());

        // descripción
        String descripcion = textDescripcion.getText().toString();
        if (descripcion.equals("")) {
            textDescripcion.setError("Campo obligatorio");
            return;
        }

        Log.e(TAG, textDescripcion.getText().toString());

        //crearEvento();
        subirImagen(uriImagen);


    }

    private List<String> obtenerContaminantes() {
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
    }

    private void crearEvento() {

    }

    private void subirImagen(Uri uriImagen) {
        stopLocationUpdates();
        //File file = FileUtils.getFile(this, uriImagen);
        File file = new File(getRealPathFromURI(uriImagen));
        Log.e(TAG, file.getPath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("newimage", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        /*
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(uriImagen)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String filenameString = file.getName();
        RequestBody filename =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, filenameString);
        */

        Log.e(TAG, filename.toString());

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.uploadImagen(parts, filename);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    if (json.get("resultado").getAsBoolean()) {
                        Toast.makeText(getApplicationContext(), "SUBIDA", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "no subida", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "no successful", Toast.LENGTH_LONG).show();
                }

                startLocationUpdates();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                startLocationUpdates();
            }
        });

    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}
