package com.example.cuidadodelambiente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.ui.fragments.eventos.view.EventosLimpiezaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        CrearEventoFragment.OnEventoCreado {

    //public final ParticipaEventosFragment participaEventos = new ParticipaEventosFragment();

    private ArrayList<Fragment> listaFragmentos = new ArrayList<Fragment>();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cambia el color del status bar a verde

        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        //-------------------------------------------

        // iniciando el fragment principal
        loadFragment(DeclaracionFragments.eventosLimpiezaFragmentFragement);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        /*
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_eventos, R.id.navigation_eventos_para_ti,
                R.id.navigation_reportes, R.id.navigation_cuenta)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);*/




        /*
        try {
            RunDatabaseHelper helper = new RunDatabaseHelper(this);
            helper.close();
            Toast.makeText(getApplicationContext(), "BIEN", Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }*/

    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.eventosLimpiezaFragment:
                cambiarFragment(DeclaracionFragments.eventosLimpiezaFragmentFragement, "EVENTO");
                return true;

            case R.id.eventosRecomendados:
                cambiarFragment(DeclaracionFragments.multiselection, "REEVENTO");
                return true;

            case R.id.reportes:
                cambiarFragment(DeclaracionFragments.recomendacionCrearEventoFragment, "RECOMENDACION");
                return true;

            case R.id.perfil_usuario:
                cambiarFragment(DeclaracionFragments.perfilUsuario, "PERFIL");
                return true;
        }

        return false;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "EVENTO")
                    .commit();

            listaFragmentos.add(fragment);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if(listaFragmentos.size() > 1) {
            quitarFragmentDeLista();
        }
        else {
            listaFragmentos.clear();

            super.onBackPressed();
        }


        /*
        if(FragmentSingleton.getOldFragment() != null)
        {
            if(FragmentSingleton.getCurrentFragment().isVisible() &&
                FragmentSingleton.getOldFragment().isHidden())
            {
                getSupportFragmentManager().beginTransaction()
                        .hide(FragmentSingleton.getCurrentFragment())
                        .show(FragmentSingleton.getOldFragment())
                        .commit();
            }
        }*/
        //else
        //{
        //}
    }

    public void cambiarFragment(Fragment fragment, String tag)
    {
        Fragment currentFragment = listaFragmentos.get(listaFragmentos.size() - 1);

        if(fragment != currentFragment) {

            Log.e("MAIN","entró a cambiarFragment");

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (fragment.isAdded()) { // si ya se mostró antes
                transaction.hide(currentFragment).show(fragment);
            } else { // si es la primera vez en mostrarse
                transaction.hide(currentFragment)
                        .add(R.id.nav_host_fragment, fragment, tag);
            }

            listaFragmentos.add(fragment);
            transaction.commit();

            currentFragment.onDetach();

        }
    }

    private void quitarFragmentDeLista()
    {
        if(listaFragmentos.size() > 1)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment currentFragment = listaFragmentos.get(listaFragmentos.size() - 1);
            Fragment fragmentDestion = listaFragmentos.get(listaFragmentos.size() - 2);


            if(currentFragment.getTag() == "CREAR" || currentFragment.getTag() == "CREAR_REPORTE") {
                transaction.remove(currentFragment).show(fragmentDestion);
            }
            else {
                transaction.hide(currentFragment).show(fragmentDestion);
            }

            transaction.commit();


            listaFragmentos.remove(listaFragmentos.size() - 1);
        }
    }

    private Fragment getCurrentFragment()
    {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        return fragmentManager.getFragments().get(0);

    }

    public void cambiarVisibilidadBottomNavigation(int visibility) {
        bottomNavigationView.setVisibility(visibility);
    }

    @Override
    public void onEventoCreado(LatLng ubicacion) {
        Utilidades.iniciarFragment(getSupportFragmentManager().beginTransaction(),
                DeclaracionFragments.eventosLimpiezaFragmentFragement, "EVENTO");

        EventosLimpiezaFragment eventosLimpiezaFragment = (EventosLimpiezaFragment) getSupportFragmentManager().findFragmentByTag("EVENTO");
        eventosLimpiezaFragment.recargar();
        eventosLimpiezaFragment.moverMapa(ubicacion);

    }
}
