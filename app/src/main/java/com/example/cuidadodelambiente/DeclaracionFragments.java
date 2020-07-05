package com.example.cuidadodelambiente;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.Fragments.CrearReporteFragment;
import com.example.cuidadodelambiente.ui.fragments.participaciones.view.ParticipaEventos;
import com.example.cuidadodelambiente.ui.fragments.eventos.view.EventosLimpieza;
import com.example.cuidadodelambiente.ui.fragments.reportes.view.ReportesContaminacionFragment;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.view.RecomendacionEventosFragment;


/* inicializa los fragments principales que se usan en la aplicacion para solo
* crearlos una vez */
public class DeclaracionFragments {
    public static int actualAmbientalista = 1;

    public static EventosLimpieza eventosLimpiezaFragement = new EventosLimpieza();
    public static final Fragment recomendacionEventosFragment = new RecomendacionEventosFragment();
    public static final Fragment recomendacionCrearEventoFragment = new ReportesContaminacionFragment();
    public static final Fragment participaEventos = new ParticipaEventos();
    public static CrearEventoFragment crearEventoFragment = new CrearEventoFragment();
    public static final Fragment crearReporteFragment = new CrearReporteFragment();
    public static final Activity crearReporteActivity = new ActividadCrearReporte();

    public static Fragment getParticipaEventosFragment()
    {
        return participaEventos;

    }
}
