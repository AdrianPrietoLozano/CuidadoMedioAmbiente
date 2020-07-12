package com.example.cuidadodelambiente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActividadCrearReporte extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final int PERMISSION_ID = 40;
    private final int REQUEST_CHECK_SETTINGS = 50;
    private final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private final int UPDATE_INTERVAL_MILLISECONDS = 20000; // 20 segundos
    private final int FASTEST_UPDATE_INTERVAL_MILLISECONDS =
            UPDATE_INTERVAL_MILLISECONDS / 2;
    private final String TAG = ActividadCrearReporte.class.getSimpleName();

    private EditText descripcion;
    private ImageView fotoReporte;
    private TextView fechaHoraReporte;
    private TextView txtDireccionReporte; // direccion completa
    private TextView txtUbicacionReporte; // latitud y longitud
    private ProgressBar progressBarDireccion;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mLastLocationAux;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingRequest;

    private AddressResultReceiver resultReceiver;
    private String addressOutput;
    private boolean solicitandoDireccion;

    private boolean ubicacionObtenida;
    private boolean direccionObtenida;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_crear_reporte);


        // Cambia el color del status bar a verde
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        fotoReporte = findViewById(R.id.fotoReporte);
        fotoReporte.setOnClickListener(listenerElegirFoto);

        txtUbicacionReporte = findViewById(R.id.txtLatitudLongitud);
        txtDireccionReporte = findViewById(R.id.textViewDireccion);
        progressBarDireccion = findViewById(R.id.progressBarDireccion);
        fechaHoraReporte = findViewById(R.id.textViewFechaHora);

        descripcion = findViewById(R.id.editTextDescripcion);



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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy,  HH:mm");
        Date date = new Date();
        fechaHoraReporte.setText(dateFormat.format(date));

    }

    private View.OnClickListener listenerElegirFoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent, "Selecciona la fotografía"), 10);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Uri path = data.getData();
            fotoReporte.setImageURI(path);
        }

        switch (requestCode) {
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

}
