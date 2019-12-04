package com.example.cuidadodelambiente;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/* Clase con métodos utilizados en otras clases */
public class Utilidades {

    /* MAPAS */

    // defina ubicación del centro de Guadalajara
    public static final CameraPosition GDL = new CameraPosition.Builder()
            .target(new LatLng(20.6741519,-103.3526335))
            .zoom(15.5f).bearing(0).tilt(25).build();


    // agrega un marcador al mapa, se le puede indicar el color del marcador
    public static void agregarMarcadorMapa(GoogleMap map, LatLng posicion, int tag, float color)
    {
        map.addMarker(new MarkerOptions()
                .position(posicion)
                .icon(BitmapDescriptorFactory.defaultMarker(color))).setTag(tag);
    }

    // agrega un marcador al mapa, el color del marcador es el por defecto
    public static void agregarMarcadorMapa(GoogleMap map, LatLng posicion, int tag)
    {
        map.addMarker(new MarkerOptions().position(posicion)).setTag(tag);
    }


    /* INICIAR FRAGMENTS */
    public static void iniciarFragment(FragmentTransaction transaction, Fragment fragment, String tag)
    {
        if (fragment.isAdded()) { // si ya se mostró antes
            transaction.hide(FragmentSingleton.getCurrentFragment()).show(fragment);
        }
        else // si es la primera vez en mostrarse
        {
            transaction.hide(FragmentSingleton.getCurrentFragment())
                    .add(R.id.nav_host_fragment, fragment, tag);
        }

        transaction.addToBackStack(null);
        FragmentSingleton.setOldFragment(FragmentSingleton.getCurrentFragment());
        FragmentSingleton.setCurrentFragment(fragment);
        transaction.commit();
    }

    public static boolean hayConexionInternet(Context context)
    {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
}
