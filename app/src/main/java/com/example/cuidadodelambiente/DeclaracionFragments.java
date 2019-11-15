package com.example.cuidadodelambiente;

import androidx.fragment.app.Fragment;

import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.Fragments.CuentaFragment;
import com.example.cuidadodelambiente.Fragments.EventosLimpieza;
import com.example.cuidadodelambiente.Fragments.MisEventosFragment;
import com.example.cuidadodelambiente.Fragments.RecomendacionCrearEventoFragment;
import com.example.cuidadodelambiente.Fragments.RecomendacionEventosFragment;


/* inicializa los fragments principales que se usan en la aplicacion para solo
* crearlos una vez */
public class DeclaracionFragments {
    public static final int actualAmbientalista = 1;

    public static final Fragment eventosLimpiezaFragement = new EventosLimpieza();
    public static final Fragment recomendacionEventosFragment = new RecomendacionEventosFragment();
    public static final Fragment recomendacionCrearEventoFragment = new RecomendacionCrearEventoFragment();
    public static final Fragment cuentaFragment = new CuentaFragment();
    public static final Fragment crearEventoFragment = new CrearEventoFragment();
}
