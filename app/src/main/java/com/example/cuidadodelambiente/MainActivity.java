package com.example.cuidadodelambiente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.cuidadodelambiente.Fragments.BlankFragment;
import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.Fragments.CuentaFragment;
import com.example.cuidadodelambiente.Fragments.FragmentsReportes;
import com.example.cuidadodelambiente.Fragments.MisEventosFragment;
import com.example.cuidadodelambiente.Fragments.RecomendacionCrearEventoFragment;
import com.example.cuidadodelambiente.Fragments.RecomendacionEventosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private final Fragment eventosFragment = new BlankFragment();
    private final Fragment reportesFragment = new FragmentsReportes();
    private final Fragment misEventosFragment = new MisEventosFragment();
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
            case R.id.eventos:
                iniciarFragment(eventosFragment);
                return true;

            case R.id.reportes:
                iniciarFragment(reportesFragment);
                return true;

            case R.id.mis_eventos:
                iniciarFragment(misEventosFragment);
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
