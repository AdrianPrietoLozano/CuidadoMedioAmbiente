package com.example.cuidadodelambiente;

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
    public static void agregarMarcadorMapa(GoogleMap map, LatLng posicion, String titulo, float color)
    {
        map.addMarker(new MarkerOptions()
                .position(posicion)
                .icon(BitmapDescriptorFactory.defaultMarker(color))
                .title(titulo));
    }

    // agrega un marcador al mapa, el color del marcador es el por defecto
    public static void agregarMarcadorMapa(GoogleMap map, LatLng posicion, String titulo)
    {
        map.addMarker(new MarkerOptions().position(posicion).title(titulo));
    }


    /* INICIAR FRAGMENTS */
    public static void iniciarFragment(FragmentTransaction transaction, Fragment fragment)
    {
        if (fragment.isAdded()) { // si ya se mostró antes
            transaction.hide(FragmentSingleton.getCurrentFragment()).show(fragment);
        }
        else // si es la primera vez en mostrarse
        {
            transaction.hide(FragmentSingleton.getCurrentFragment())
                    .add(R.id.nav_host_fragment, fragment, "currentFragment");
        }

        //transaction.addToBackStack(null);
        FragmentSingleton.setOldFragment(FragmentSingleton.getCurrentFragment());
        FragmentSingleton.setCurrentFragment(fragment);
        transaction.commit();
    }
}
