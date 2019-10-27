package com.example.cuidadodelambiente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.cuidadodelambiente.Fragments.EventosLimpieza;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private boolean primeraVezIniciarApp = true; // para saber si es la primera vez que inicia la app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cambia el color del status bar a verde
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.verde3));
        //-------------------------------------------

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Utilidades.iniciarFragment(getSupportFragmentManager().beginTransaction(),
                DeclaracionFragments.eventosLimpiezaFragement);
        bottomNavigationView.setSelectedItemId(R.id.eventosLimpieza);




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


    // pasar contexto a Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.eventosLimpieza:
                // si es la primera vez que inicia la app se inicializa
                // el fragment principal
                if(primeraVezIniciarApp)
                {
                    DeclaracionFragments.eventosLimpiezaFragement = new EventosLimpieza();
                    primeraVezIniciarApp = false;
                }

                Utilidades.iniciarFragment(getSupportFragmentManager().beginTransaction(),
                        DeclaracionFragments.eventosLimpiezaFragement);

                return true;

            case R.id.eventosRecomendados:
                Utilidades.iniciarFragment(getSupportFragmentManager().beginTransaction(),
                        DeclaracionFragments.recomendacionEventosFragment);
                return true;

            case R.id.reportes:
                Utilidades.iniciarFragment(getSupportFragmentManager().beginTransaction(),
                        DeclaracionFragments.recomendacionCrearEventoFragment);
                return true;

            case R.id.cuenta:
                Utilidades.iniciarFragment(getSupportFragmentManager().beginTransaction(),
                        DeclaracionFragments.cuentaFragment);
                return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {

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
            super.onBackPressed();
        //}
    }
}
