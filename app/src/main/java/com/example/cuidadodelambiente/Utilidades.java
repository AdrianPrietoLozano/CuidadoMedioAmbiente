package com.example.cuidadodelambiente;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/* Clase con métodos utilizados en otras clases */
public class Utilidades {

    /* MAPAS */

    public static final LatLng DEFAULT_POSITION_MAP = new LatLng(20.6741519,-103.3526335);

    // defina ubicación del centro de Guadalajara
    public static final CameraPosition GDL = new CameraPosition.Builder()
            .target(DEFAULT_POSITION_MAP)
            .zoom(12.5f).bearing(0).tilt(25).build();


    // agrega un marcador al mapa, se le puede indicar el color del marcador
    public static void agregarMarcadorMapa(GoogleMap map, LatLng posicion, Integer tag, float color)
    {
        map.addMarker(new MarkerOptions()
                .position(posicion)
                .icon(BitmapDescriptorFactory.defaultMarker(color))).setTag(tag);
    }

    // agrega un marcador al mapa, el color del marcador es el por defecto
    public static void agregarMarcadorMapa(GoogleMap map, LatLng posicion, Integer tag)
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
        try {
            ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = con.getActiveNetworkInfo();

            return (networkInfo != null && networkInfo.isConnected());
        } catch (Exception e) {
            return true;
        }
    }

    public static void cambiarColorStatusBar(Window w, int color) {
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(color);
    }

    public static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
