package com.example.cuidadodelambiente.data.network;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

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

public class ActualizacionesUbicacionHelper implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final int PERMISSION_LOCATION_ID = 40;
    private final int REQUEST_CHECK_SETTINGS = 50;
    private final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private int updateIntervalMilliseconds;
    private int fastestUpdateIntervalMilliseconds;

    private Activity activity;
    private OnActualizacionUbicacion listener;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingRequest;

    public interface OnActualizacionUbicacion {
        void onLocationChanged(Location location);
        void onConnectionFailed();
        void onPermissionError(String error);
        void onError(String error);
    }

    public ActualizacionesUbicacionHelper(Activity activity, OnActualizacionUbicacion listener,
                                          int updateIntervalMilliseconds) {
        this.activity = activity;
        this.listener = listener;
        this.updateIntervalMilliseconds = updateIntervalMilliseconds;
        this.fastestUpdateIntervalMilliseconds = this.updateIntervalMilliseconds / 2;

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingRequest();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.activity.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(this.updateIntervalMilliseconds);
        mLocationRequest.setFastestInterval(this.fastestUpdateIntervalMilliseconds);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        mLocationSettingRequest = builder.build();

    }

    public void iniciarObtenerUbicacion() {
        if (revisarPermisos()) {
            if (isLocationEnabled()) {
                if (revisarPermisosGooglePlay()) {

                    // preparar solicitud de conexión
                    mGoogleApiClient.connect();
                }
            }
            else {
                revisarConfiguracionUbicacion();
            }
        } else {
            solicitarPermisoUbicacion();
        }
    }

    // revisa si se tienen los permisos para acceder a la ubicación del usuario
    private boolean revisarPermisos(){
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    public void conectar(){
        if (revisarPermisos() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    // revisa si la ubicación esta activada
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private boolean revisarPermisosGooglePlay() {
        int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.activity);
        if(checkGooglePlayServices != ConnectionResult.SUCCESS) {
            /*

            google play services is missing or update is required
            return code could be:
            SUCCESS,
            SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
            SERVICE_DISABLED, SERVICE_INVALID
             */
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this.activity, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;
    }

    protected void revisarConfiguracionUbicacion() {
        // esta línea es necesaria para poder mostrar el dialogo para activar ubicación
        if (mGoogleApiClient != null) mGoogleApiClient.connect();

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
                        iniciarActualizacionesUbicacion();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("RESOLUTION", "MOSTRAR_DIALOGO");
                        try {
                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("CATCH", "No se pudo crear el dialogo");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("UNAVAILABLE", "Inaccesibles");
                        //finish();
                        listener.onError("Inaccesibles");
                        break;
                }
            }
        });
    }

    // pide permisos al usuario para acceder a su ubicación
    private void solicitarPermisoUbicacion(){
        ActivityCompat.requestPermissions(
                this.activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_LOCATION_ID
        );
    }

    public void iniciarActualizacionesUbicacion() {
        if (!mGoogleApiClient.isConnected())
            return;

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

    public void desconectar() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            /*
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(location != null) {
            //startIntentService(); // intentar obtener la dirección del usuario
            listener.onLocationChanged(location);
        }*/

        iniciarActualizacionesUbicacion();
    }

    public void detenerActualizacionesUbicacion() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    //iniciarObtenerUbicacion();
                    iniciarActualizacionesUbicacion();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //Toast.makeText(getApplicationContext(), "Permiso necesario", Toast.LENGTH_SHORT).show();
                    listener.onPermissionError("Permiso necesario");
                }

                break;

            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //Toast.makeText(getApplicationContext(), "Google Play Services es necesario", Toast.LENGTH_SHORT).show();
                    listener.onError("Google Play Services es necesario");
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // ver si esto es necesario
                //conectar();

                iniciarObtenerUbicacion();
            } else {
                Log.e("HELPER", "Error");
                listener.onPermissionError("Permiso de ubicación es necesario");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(getApplicationContext(), "onConnectionSuespended", Toast.LENGTH_SHORT).show();
        Log.e("HELPER", "onConnectionSuespended");
        listener.onConnectionFailed();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(getApplicationContext(), "OnConnectionFailed", Toast.LENGTH_SHORT).show();
        Log.e("HELPER", "OnConnectionFailed");
        listener.onConnectionFailed();
    }

    @Override
    public void onLocationChanged(Location location) {
        //mLastLocationAux = location;

        listener.onLocationChanged(location);

        // intentar obtener la dirección del usuario
        //startIntentService();
    }
}