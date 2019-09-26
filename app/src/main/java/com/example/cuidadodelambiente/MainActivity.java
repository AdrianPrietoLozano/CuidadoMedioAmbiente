package com.example.cuidadodelambiente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private BottomNavigationView bottomNavigationView;
    private final Fragment eventosFragment = new BlankFragment();
    private final Fragment eventosRecomendadosFragment = new RecomendacionEventosFragment();
    private final Fragment recomendacionCrearEventoFragment = new RecomendacionCrearEventoFragment();
    private final Fragment cuentaFragment = new CuentaFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //bottomNavigationView.setSelectedItemId(R.id.todos_eventos);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.todos_eventos:
                iniciarFragment(eventosFragment);
                return true;

            case R.id.recomendacion_eventos:
                iniciarFragment(eventosRecomendadosFragment);
                return true;

            case R.id.recomendacion_crea_evento:
                iniciarFragment(recomendacionCrearEventoFragment);
                return true;

            case R.id.cuenta:
                iniciarFragment(cuentaFragment);
                return true;

        }

        return false;
    }

    private void iniciarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
    }

}
